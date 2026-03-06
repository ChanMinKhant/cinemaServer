const bcrypt = require("bcrypt");
const fs = require("fs");

// ==============================
// CLI ARG PARSE
// ==============================
const args = process.argv.slice(2);
const countIndex = args.indexOf("--count");

if (countIndex === -1 || !args[countIndex + 1]) {
  console.error("❌ Usage: node gen-users.js --count <number>");
  process.exit(1);
}

const COUNT = parseInt(args[countIndex + 1], 10);
if (isNaN(COUNT) || COUNT <= 0) {
  console.error("❌ --count must be a positive number");
  process.exit(1);
}

// ==============================
// CONFIG
// ==============================
const PASSWORD = "123123123";
const SALT_ROUNDS = 10;
const OUTPUT_FILE = "users_seed.sql";

// ==============================
// MAIN
// ==============================
(async () => {
  try {
    const hash = await bcrypt.hash(PASSWORD, SALT_ROUNDS);

    let sql = `-- AUTO-GENERATED USERS\n`;
    sql += `-- Password for all users: ${PASSWORD}\n`;
    sql += `-- Total users: ${COUNT}\n\n`;

    sql += `INSERT INTO users (username, password, email, phone, balance, role)\nVALUES\n`;

    const rows = [];

    for (let i = 1; i <= COUNT; i++) {
      const username = `user${i}`;              // one word ✔
      const email = `user${i}@example.com`;
      const phone = `+9597000${String(i).padStart(4, "0")}`;
      const balance = Math.floor(Math.random() * 100000);
      const role = "user";

      rows.push(
        `('${username}', '${hash}', '${email}', '${phone}', ${balance}, '${role}')`
      );
    }

    sql += rows.join(",\n") + ";\n";

    fs.writeFileSync(OUTPUT_FILE, sql);
    console.log(`✅ Generated ${COUNT} users → ${OUTPUT_FILE}`);
  } catch (err) {
    console.error("❌ Error:", err.message);
  }
})();