const express = require("express");
const cors = require("cors");
const routes = require("./routes");

const app = express();

app.use(cors());
app.use(express.json());

app.use("/api/v1", routes);

app.get("/", (req, res) => {
  res.json({
    message: "API AmigOjo Live funcionando correctamente",
  });
});

module.exports = app;