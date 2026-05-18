const express = require("express");
const cors = require("cors");
const path = require("path");
const fs = require("fs");

const routes = require("./routes");
const errorMiddleware = require("./middlewares/error.middleware");

const app = express();

const PUBLIC_DIR = path.resolve(__dirname, "../public");
const FRONTEND_DIST = path.resolve(__dirname, "../../frontend/dist");

const allowedOrigins = [
  'https://amigojolive.onrender.com',
  'http://localhost:5173',
  'http://localhost:4173',
];
if (process.env.FRONTEND_URL) {
  allowedOrigins.push(process.env.FRONTEND_URL);
}
app.use(
  cors({
    origin(origin, callback) {
      if (!origin || allowedOrigins.includes(origin)) {
        callback(null, true);
      } else {
        callback(new Error(`CORS: origen no permitido: ${origin}`));
      }
    },
    credentials: true,
  })
);
app.use(express.json());
const imagesDir = path.resolve(__dirname, "../public/images");
const documentsDir = path.resolve(__dirname, "../public/documents");

app.use(
  "/public/images",
  express.static(imagesDir, {
    setHeaders(res) {
      res.setHeader("Cache-Control", "public, max-age=86400");
    },
  })
);

app.use(
  "/public/documents",
  express.static(documentsDir, {
    setHeaders(res, filePath) {
      const filename = path.basename(filePath);
      res.setHeader("Content-Disposition", `attachment; filename="${filename}"`);
      res.setHeader("Cache-Control", "public, max-age=86400");
    },
  })
);

// Capturar 404 para archivos públicos inexistentes en lugar de devolver index.html
app.use("/public", (req, res) => {
  res.status(404).send("Archivo no encontrado");
});

app.use("/api/v1", routes);

const frontendExists = fs.existsSync(FRONTEND_DIST);

if (frontendExists) {
  app.use(express.static(FRONTEND_DIST));
  app.get("*", (req, res) => {
    res.sendFile(path.join(FRONTEND_DIST, "index.html"));
  });
} else {
  app.get("/", (req, res) => {
    res.json({ message: "API AmigojoLive funcionando correctamente" });
  });
}

app.use(errorMiddleware);

module.exports = app;