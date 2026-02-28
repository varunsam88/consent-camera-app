const express = require('express');
const multer = require('multer');
const path = require('path');

const uploadDir = path.join(__dirname, 'uploads');
const storage = multer.diskStorage({
  destination: (req, file, cb) => cb(null, uploadDir),
  filename: (req, file, cb) => cb(null, file.originalname)
});
const upload = multer({ storage });

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.static('uploads'));

app.post('/upload', upload.single('file'), (req, res) => {
  if (!req.file) {
    return res.status(400).send('No file uploaded');
  }
  return res.send(`File received: ${req.file.originalname}`);
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}, uploads directory: ${uploadDir}`);
});
