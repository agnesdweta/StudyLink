const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

const app = express();
app.use(cors());
app.use(express.json());
app.use(bodyParser.json());


app.get('/', (req, res) => {
    res.send('StudyLink API is running 🚀');
}); 


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

    console.log("USER BARU:", newUser);

    res.json({
        message: 'Register berhasil',
        username
    });
});


// ===== Endpoint Login =====
app.post('/login', async (req, res) => {
    console.log("BODY:", req.body);
    const { username, password } = req.body;

    const user = users.find(u => u.username === username);
    if (!user) return res.status(401).json({ message: 'Username / Password salah' });

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) return res.status(401).json({ message: 'Username / Password salah' });

    const token = jwt.sign(
        { id: user.id, username: user.username },
        'secretkey',
        { expiresIn: '1h' }
    );

    res.json({
        message: 'Login Berhasil',
        token,
        username: user.username
    });
});

// ===== CRUD FORUM (POSTS) =====
let posts = [
    { id: 1, title: "Post Pertama", content: "Halo Forum!" }
];

// CREATE
app.post('/posts', (req, res) => {
    const newPost = {
        id: posts.length + 1,
        title: req.body.title,
        content: req.body.content
    };
    posts.push(newPost);
    res.json(newPost);
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


// ===== JALANKAN SERVER (PALING BAWAH) =====
app.listen(3000, () => {
    console.log('Server running on http://localhost:3000');
});
