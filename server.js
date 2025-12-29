const express = require('express');
const cors = require('cors');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

const app = express();
app.use(cors());
app.use(express.json());

const SECRET_KEY = "secretkey";

function authenticateToken(req, res, next) {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    if (!token) {
        return res.status(401).json({ message: 'Token tidak ditemukan' });
    }

    jwt.verify(token, SECRET_KEY, (err, user) => {
        if (err) {
            return res.status(403).json({ message: 'Token tidak valid' });
        }
        req.user = user;
        next();
    });
}

// ===== Dummy User =====
const users = [
    { 
        id: 1, 
        username: 'admin', 
        password: '$2b$10$V5DO7w9qYAupjFDEp6UMqOjh4zR.0Xcna6/cc6U05VRnafmQZvboe' 
        // password asli: 12345
    }
];

// ===== Endpoint Register =====
app.post('/register', async (req, res) => {
    const { username, password } = req.body;

    // validasi
    if (!username || !password) {
        return res.status(400).json({ message: 'Data tidak lengkap' });
    }

    // cek username sudah ada
    const existingUser = users.find(u => u.username === username);
    if (existingUser) {
        return res.status(409).json({ message: 'Username sudah digunakan' });
    }

    // hash password
    const hashedPassword = await bcrypt.hash(password, 10);

    // simpan user baru
    const newUser = {
        id: users.length + 1,
        username,
        password: hashedPassword
    };

    users.push(newUser);
    const token = jwt.sign(
        { id: newUser.id, username: newUser.username },
        SECRET_KEY,
        { expiresIn: '1h' }
    );
    res.json({
        message: 'Register berhasil',
        username: newUser.username,
        token: token
    });
});


// ===== Endpoint Login =====
app.post('/login', async (req, res) => {
    const { username, password } = req.body;

    const user = users.find(u => u.username === username);
    if (!user) return res.status(401).json({ message: 'Username / Password salah' });

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) return res.status(401).json({ message: 'Username / Password salah' });

    const token = jwt.sign(
    { id: user.id, username: user.username },
    SECRET_KEY,
    { expiresIn: '1h' }
);
    console.log("Username:", username);
    console.log("Token:", token);
    console.log("Password:", password);  // ini password asli dari input user

    res.json({
        message: 'Login Berhasil',
        token,
        username: user.username
    });
});

// Endpoint kirim notif
app.post('/send-notification', async (req, res) => {
    const { token, title, body } = req.body;

    const message = {
        notification: { title, body },
        token: token // token device Android
    };

    try {
        const response = await admin.messaging().send(message);
        res.json({ success: true, response });
    } catch (error) {
        console.error(error);
        res.status(500).json({ success: false, error });
    }
});

// ===== CRUD FORUM (POSTS) =====
let posts = [
    { id: 1, title: "Post Pertama", content: "Halo Forum!" }
];

// CREATE
app.post('/posts', (req, res) => {
    const Post = {
        id: posts.length + 1,
        title: req.body.title,
        content: req.body.content
    };
    posts.push(Post);
    res.json(Post);
});

// READ
app.get('/posts', (req, res) => {
    res.json(posts);
});

// UPDATE
app.put('/posts/:id', (req, res) => {
    const post = posts.find(p => p.id == req.params.id);
    if (!post) return res.status(404).json({ message: "Post tidak ditemukan" });

    post.title = req.body.title;
    post.content = req.body.content;
    res.json(post);
});

// DELETE
app.delete('/posts/:id', (req, res) => {
    posts = posts.filter(p => p.id != req.params.id);
    res.json({ message: "Post dihapus" });
});

let assignments = [
    {
        id: 1,
        title: "Tugas PBO",
        course: "Pemrograman Berorientasi Objek",
        deadline: "20 Des 2025"
    }
];

// CREATE
app.post('/assignments', authenticateToken, (req, res) => {
    const assignment = {
        id: assignments.length + 1,
        title: req.body.title,
        course: req.body.course,
        deadline: req.body.deadline
    };
    assignments.push(assignment);
    res.json(assignment);
});
// READ
app.get('/assignments', authenticateToken, (req, res) => {
    res.json(assignments);
});

// UPDATE
app.put('/assignments/:id', authenticateToken, (req, res) => {
    const assignment = assignments.find(a => a.id == req.params.id);
    if (!assignment)
        return res.status(404).json({ message: "Assignment tidak ditemukan" });

    assignment.title = req.body.title;
    assignment.course = req.body.course;
    assignment.deadline = req.body.deadline;

    res.json(assignment);
});

// DELETE
app.delete('/assignments/:id', authenticateToken, (req, res) => {
    assignments = assignments.filter(a => a.id != req.params.id);
    res.json({ message: 'Assignment dihapus' });
});


let schedules = []; // array JS menggantikan database

// READ
app.get('/schedules', (req, res) => res.json(schedules));

// CREATE
app.post('/schedules', (req, res) => {
  const { title, date, time } = req.body;
  if (!title || !date || !time) return res.status(400).json({ message: 'Data tidak boleh kosong' });

  const newSchedule = { id: schedules.length > 0 ? schedules[schedules.length - 1].id + 1 : 1, title, date, time };
  schedules.push(newSchedule);
  res.json(newSchedule);
});

// UPDATE
app.put('/schedules/:id', (req, res) => {
  const id = parseInt(req.params.id);
  const { title, date, time } = req.body;

  const index = schedules.findIndex(s => s.id === id);
  if (index === -1) return res.status(404).json({ message: 'Schedule tidak ditemukan' });

  schedules[index] = { id, title, date, time };
  res.json(schedules[index]);
});

// DELETE
app.delete('/schedules/:id', (req, res) => {
  const id = parseInt(req.params.id);
  const initialLength = schedules.length;
  schedules = schedules.filter(s => s.id !== id);

  if (schedules.length === initialLength) return res.status(404).json({ message: 'Schedule tidak ditemukan' });
  res.json({ success: true });
});


let exams = [
  { id: 1, title: "Matematika Dasar", course: "Matematika", date: "2025-12-26" },
  { id: 2, title: "Bahasa Indonesia", course: "Bahasa", date: "2025-12-26" }
];

// READ semua ujian
app.get("/exams", (req, res) => {
  res.json(exams);
});

// CREATE ujian baru
app.post("/exams", (req, res) => {
  const newExam = { id: exams.length + 1, ...req.body };
  exams.push(newExam);
  res.json(newExam);
});

// UPDATE ujian
app.put("/exams/:id", (req, res) => {
  const id = parseInt(req.params.id);
  exams = exams.map(e => e.id === id ? { ...e, ...req.body } : e);
  res.json(exams.find(e => e.id === id));
});
// DELETE ujian
app.delete("/exams/:id", (req, res) => {
  const id = parseInt(req.params.id);
  exams = exams.filter(e => e.id !== id);
  res.json({ success: true });
});

let courses = [
    {
        id: 1,
        name: "Pemrograman Berorientasi Objek",
        description: "Mempelajari OOP dengan Java",
        time: "08:00 - 10:00",
        instructor: "Pak Budi"
    }
];

// CREATE
app.post('/courses', authenticateToken, (req, res) => {
    const course = {
        id: courses.length + 1,
        name: req.body.name,
        description: req.body.description,
        time: req.body.time,
        instructor: req.body.instructor
    };
    courses.push(course);
    res.json(course);
});

// READ
app.get('/courses', authenticateToken, (req, res) => {
    res.json(courses);
});

// UPDATE
app.put('/courses/:id', authenticateToken, (req, res) => {
    const id = parseInt(req.params.id);
    const index = courses.findIndex(c => c.id === id);
    if(index === -1) return res.status(404).json({error: "Course not found"});

    const { name, description, time, instructor } = req.body;
    courses[index] = { id, name, description, time, instructor };
    res.json(courses[index]);
});

// DELETE
app.delete('/courses/:id', authenticateToken, (req, res) => {
    const id = parseInt(req.params.id);
    const index = courses.findIndex(c => c.id === id);
    if(index === -1) return res.status(404).json({error: "Course not found"});

    courses.splice(index, 1);
    res.json({ success: true });
});


// ===== JALANKAN SERVER (PALING BAWAH) =====
app.listen(3000, () => {
    console.log('Server running on http://localhost:3000');
});
