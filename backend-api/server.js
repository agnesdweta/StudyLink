const express = require('express');
const cors = require('cors');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const fs = require('fs');
const multer = require('multer');
const path = require('path');

const app = express();
const PORT = 3000;
const SECRET_KEY = "secretkey";
const DB_FILE = 'db.json';

// ===== Body parser =====
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use('/uploads', express.static('uploads'));

// ===== Multer setup =====
const storage = multer.diskStorage({
  destination: (req, file, cb) => cb(null, 'uploads/'),
  filename: (req, file, cb) => cb(null, Date.now() + '-' + file.originalname)
});
const upload = multer({ storage });

// ===== Load & Save DB =====
function loadDB() {
  if (!fs.existsSync(DB_FILE)) return { users: [], assignments: [] };
  return JSON.parse(fs.readFileSync(DB_FILE));
}
function saveDB(data) {
  fs.writeFileSync(DB_FILE, JSON.stringify(data, null, 2));
}

// ===== JWT Middleware =====
function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) return res.status(401).json({ message: 'Token tidak ditemukan' });

  jwt.verify(token, SECRET_KEY, (err, user) => {
    if (err) return res.status(403).json({ message: 'Token tidak valid' });
    req.user = user;
    next();
  });
}

// ===== REGISTER =====
app.post('/register', async (req, res) => {
  const { username, password } = req.body || {};
  if (!username || !password) return res.status(400).json({ message: 'Data tidak lengkap' });

  const db = loadDB();
  if (db.users.find(u => u.username === username)) {
    return res.status(409).json({ message: 'Username sudah digunakan' });
  }

  const hashedPassword = await bcrypt.hash(password, 10);
  const newUser = { id: Date.now(), username, password: hashedPassword };
  db.users.push(newUser);
  saveDB(db);

  const token = jwt.sign({ id: newUser.id, username: newUser.username }, SECRET_KEY, { expiresIn: '1h' });
  res.json({ message: 'Register berhasil', username: newUser.username, token });
});

// ===== LOGIN =====
app.post('/login', async (req, res) => {
  const { username, password } = req.body || {};
  if (!username || !password) return res.status(400).json({ message: 'Data tidak lengkap' });

  const db = loadDB();
  const user = db.users.find(u => u.username === username);
  if (!user) return res.status(401).json({ message: 'Username / Password salah' });

  const match = await bcrypt.compare(password, user.password);
  if (!match) return res.status(401).json({ message: 'Username / Password salah' });

  const token = jwt.sign({ id: user.id, username: user.username }, SECRET_KEY, { expiresIn: '1h' });
  res.json({ message: 'Login berhasil', token, username: user.username });
});

// ===== ASSIGNMENTS CRUD =====

// GET all
app.get('/assignments', authenticateToken, (req, res) => {
  const db = loadDB();
  res.json(db.assignments);
});

// CREATE
app.post('/assignments', authenticateToken, upload.single('image'), (req, res) => {
  const db = loadDB();
  const { title, course, deadline } = req.body;
  if (!title || !course || !deadline) return res.status(400).json({ message: 'Data tidak lengkap' });

  const newAssignment = {
    id: Date.now(),
    title,
    course,
    deadline,
    image: req.file ? req.file.filename : null
  };
  db.assignments.push(newAssignment);
  saveDB(db);
  res.json(newAssignment);
});

// UPDATE
app.put('/assignments/:id', authenticateToken, upload.single('image'), (req, res) => {
  const db = loadDB();
  const idx = db.assignments.findIndex(a => a.id == req.params.id);
  if (idx === -1) return res.status(404).json({ message: 'Assignment tidak ditemukan' });

  const { title, course, deadline } = req.body;
  if (title) db.assignments[idx].title = title;
  if (course) db.assignments[idx].course = course;
  if (deadline) db.assignments[idx].deadline = deadline;
  if (req.file) db.assignments[idx].image = req.file.filename;

  saveDB(db);
  res.json(db.assignments[idx]);
});

// DELETE
app.delete('/assignments/:id', authenticateToken, (req, res) => {
  const db = loadDB();
  db.assignments = db.assignments.filter(a => a.id != req.params.id);
  saveDB(db);
  res.json({ message: 'Assignment dihapus' });
});

// ===== START SERVER =====
app.listen(PORT, () => console.log(`Server running on http://localhost:${PORT}`));
