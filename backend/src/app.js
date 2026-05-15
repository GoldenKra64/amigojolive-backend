const express = require("express");
const cors = require("cors");
const path = require("path");
const fs = require("fs");

const routes = require("./routes");
const errorMiddleware = require("./middlewares/error.middleware");

const app = express();

const PUBLIC_DIR = path.resolve(__dirname, "../public");
const FRONTEND_DIST = path.resolve(__dirname, "../../frontend/dist");

app.use(cors());
app.use(express.json());
app.use(
  "/public",
  express.static(PUBLIC_DIR, {
    setHeaders(res, filePath) {
      if (filePath.includes(`${path.sep}documents${path.sep}`)) {
        res.setHeader("Content-Disposition", "attachment");
      }
      res.setHeader("Cache-Control", "public, max-age=86400");
    },
  })
);

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