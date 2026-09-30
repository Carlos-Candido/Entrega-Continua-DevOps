const express = require("express");
const {
  makeWASocket,
  useMultiFileAuthState
} = require("@whiskeysockets/baileys");

const app = express();

app.use(express.json());

let sock;

async function start() {

  const { state, saveCreds } =
      await useMultiFileAuthState("auth");

  sock = makeWASocket({
    auth: state
  });

  sock.ev.on("creds.update", saveCreds);

  console.log("WhatsApp conectado");
}

start();

app.post("/send", async (req, res) => {

  try {

    const { phone, message } = req.body;

    await sock.sendMessage(
      `${phone}@s.whatsapp.net`,
      {
        text: message
      }
    );

    res.sendStatus(200);

  } catch (error) {

    console.error(error);

    res.status(500).json({
      error: error.message
    });
  }
});

app.listen(3000, () => {
  console.log("API rodando");
});