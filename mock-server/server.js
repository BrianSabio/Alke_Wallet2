const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = 3000;
const DB_FILE = path.join(__dirname, 'db.json');
const SEED_FILE = path.join(__dirname, 'db.seed.json');

app.use(cors());
app.use(express.json());

// Leer base de datos local (crea db.json a partir de db.seed.json si no existe)
function readDb() {
    try {
        if (!fs.existsSync(DB_FILE) && fs.existsSync(SEED_FILE)) {
            fs.copyFileSync(SEED_FILE, DB_FILE);
        }
        const data = fs.readFileSync(DB_FILE, 'utf8');
        return JSON.parse(data);
    } catch (e) {
        return { users: [], transactions: [] };
    }
}

// Guardar en base de datos local
function writeDb(data) {
    fs.writeFileSync(DB_FILE, JSON.stringify(data, null, 2), 'utf8');
}

// 1. POST /auth/login -> LoginResponse { accessToken, user }
app.post('/auth/login', (req, res) => {
    console.log('[POST /auth/login] Intento de login:', req.body);
    const { email } = req.body;
    const db = readDb();
    const user = db.users.find(u => u.email === email) || db.users[0];

    res.json({
        accessToken: "mock-jwt-token-alke-wallet-12345",
        user: {
            id: user.id,
            name: user.name,
            email: user.email,
            points: user.points !== undefined ? user.points : 1000,
            avatar: user.avatar || null
        }
    });
});

// 2. POST /users -> Signup/Crear usuario -> UserDto
app.post('/users', (req, res) => {
    console.log('[POST /users] Intento de registro:', req.body);
    const { name, email } = req.body;
    const db = readDb();

    const newUser = {
        id: db.users.length > 0 ? Math.max(...db.users.map(u => u.id)) + 1 : 1,
        name: name || "Usuario Nuevo",
        email: email || "nuevo@alke.com",
        points: 1000,
        avatar: null
    };

    db.users.push(newUser);
    writeDb(db);

    res.status(201).json(newUser);
});

// 3. GET /transactions -> List<TransactionDto>
app.get('/transactions', (req, res) => {
    console.log('[GET /transactions] Solicitando historial de transacciones');
    const db = readDb();
    res.json(db.transactions);
});

// 4. POST /transactions -> TransactionDto
app.post('/transactions', (req, res) => {
    console.log('[POST /transactions] Nueva transacción:', req.body);
    const { amount, concept, type, to_user_id, from_user_id } = req.body;
    const db = readDb();

    const newTransaction = {
        id: db.transactions.length > 0 ? Math.max(...db.transactions.map(t => t.id)) + 1 : 100,
        amount: Number(amount) || 0.0,
        concept: concept || "Transacción Demo",
        date: new Date().toISOString().split('T')[0],
        type: type || "send",
        to_user_id: to_user_id || 0,
        from_user_id: from_user_id || 1
    };

    db.transactions.unshift(newTransaction);

    // Actualizar saldo de puntos del usuario
    const user = db.users.find(u => u.id === (from_user_id || 1));
    if (user) {
        const numAmount = Math.floor(Number(amount) || 0);
        if (type === 'send') {
            user.points = Math.max(0, (user.points || 1000) - numAmount);
        } else {
            user.points = (user.points || 1000) + numAmount;
        }
    }

    // Incluir objeto user dentro del mismo transaction response para no romper el tipo Call<TransactionDto>
    newTransaction.user = user ? {
        id: user.id,
        name: user.name,
        email: user.email,
        points: user.points,
        avatar: user.avatar || null
    } : null;

    writeDb(db);
    res.status(201).json(newTransaction);
});

app.listen(PORT, '0.0.0.0', () => {
    console.log(`====================================================`);
    console.log(` Servidor Mock Alke Wallet escuchando en puerto ${PORT}`);
    console.log(` - Emulador Android Studio: http://10.0.2.2:${PORT}/`);
    console.log(` - Dispositivo Físico WiFi: http://<TU_IP_LOCAL>:${PORT}/`);
    console.log(`====================================================`);
});
