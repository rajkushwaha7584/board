# Live Whiteboard

Real-time collaborative whiteboard: draw on a shared canvas, see strokes sync across browsers, and persist everything in MySQL.

---

## Prerequisites

- **Java 17**
- **MySQL** (e.g. 8.x) running locally
- **Maven** (or use the included wrapper `mvnw` / `mvnw.cmd`)

---

## 1. Database setup

1. Start MySQL.
2. Create a database named `whiteboard`:
   ```sql
   CREATE DATABASE whiteboard;
   ```
3. In `src/main/resources/application.properties`, set your MySQL username and password:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=YOUR_PASSWORD
   ```
   (Update the URL/host/port if MySQL is not on `localhost:3306`.)

---

## 2. Run the application

From the project root:

**Windows (PowerShell / CMD):**
```bash
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

Wait until you see something like: `Started LiveWhiteboardApplication`. The app runs on **port 8081** by default.

---

## 3. Use the whiteboard in the browser

1. Open a browser and go to:
   ```
   http://localhost:8081
   ```
2. **Create a board** (required for saving to the database):
   - Click **"Create new board"**.
   - A new board is created in MySQL and you are connected automatically.
   - The status should show **Connected**.
3. **Draw** on the white canvas with mouse or touch. Each stroke is saved to the database and broadcast to everyone on the same board.
4. **Optional:** Change **Color** and **Width** in the toolbar before drawing.
5. **Collaboration:** Open another tab (or another device) and go to `http://localhost:8081`. Paste the same **Board ID** from the first tab, click **Connect**, and draw. You’ll see strokes from both tabs in real time.

---

## Quick reference

| What you want to do        | How |
|----------------------------|-----|
| Start drawing and save to DB | Click **Create new board**, then draw. |
| Join an existing board     | Paste the board UUID into **Board ID**, click **Connect**. |
| Share a board with someone | Send them the **Board ID** (UUID); they paste it and click **Connect**. |
| Use a specific board from URL | Open `http://localhost:8081?board=YOUR-UUID-HERE`. |

---

## API (optional)

- **Create a board:**  
  `POST http://localhost:8081/api/whiteboards`  
  Body: `{"name": "My Board"}`  
  Response: `{ "id": "...", "name": "...", "createdAt": "...", "strokes": [] }`

- **List boards:**  
  `GET http://localhost:8081/api/whiteboards`

- **Get one board (with strokes):**  
  `GET http://localhost:8081/api/whiteboards/{id}`

---

## Troubleshooting

- **"Disconnected" or strokes not saving**  
  Click **Create new board** first so a board exists in the DB and you are connected. Only then are strokes sent to the server and stored in MySQL.

- **"Could not create board"**  
  Check that the app is running (port 8081) and that MySQL is running and the `whiteboard` database exists. Check `application.properties` for correct username/password.

- **Database tables**  
  Tables `whiteboards` and `strokes` are created automatically on first run (`spring.jpa.hibernate.ddl-auto=update`). Ensure the MySQL user has rights to create/update schema.
