(function () {
  const canvas = document.getElementById("canvas");
  const ctx = canvas.getContext("2d");
  const colorInput = document.getElementById("color");
  const bgColorInput = document.getElementById("bgColor");
  const lineWidthInput = document.getElementById("lineWidth");
  const lineWidthVal = document.getElementById("lineWidthVal");
  const boardIdInput = document.getElementById("boardId");
  const statusEl = document.getElementById("status");
  const connectBtn = document.getElementById("connectBtn");

  let currentTool = "freehand";

  let currentStroke = [];
  let shapeStart = null;
  let isDrawing = false;
  let stompClient = null;
  let subscribedTopic = null;
  const authorId = "user-" + Math.random().toString(36).slice(2, 9);
  let allStrokes = [];
  let backgroundColor = "#ffffff";
  function isFreehandTool() {
    return currentTool === "freehand" || currentTool === "eraser";
  }

  function isEraser() {
    return currentTool === "eraser";
  }

  // ========by my=======
  function setTool(tool) {
    currentTool = tool;
    document.querySelectorAll(".tool-btn").forEach(function (btn) {
      btn.classList.toggle("active", btn.getAttribute("data-tool") === tool);
    });
  }
  document.querySelectorAll(".tool-btn").forEach(function (btn) {
    btn.addEventListener("click", function () {
      setTool(btn.getAttribute("data-tool"));
    });
  });

  lineWidthInput.addEventListener("input", function () {
    lineWidthVal.textContent = this.value;
  });
  bgColorInput.addEventListener("change", function () {
    backgroundColor = this.value;
    drawAll();
  });

  function resizeCanvas() {
    const wrap = canvas.parentElement;
    const dpr = Math.min(window.devicePixelRatio || 1, 2);
    const w = Math.max(100, wrap.clientWidth);
    const h = Math.max(100, wrap.clientHeight);
    canvas.width = Math.floor(w * dpr);
    canvas.height = Math.floor(h * dpr);
    canvas.style.width = w + "px";
    canvas.style.height = h + "px";
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.scale(dpr, dpr);
    ctx.lineCap = "round";
    ctx.lineJoin = "round";
    drawAll();
  }
  window.addEventListener("resize", resizeCanvas);
  window.addEventListener("orientationchange", resizeCanvas);
  resizeCanvas();

  function getCanvasPoint(e) {
    const rect = canvas.getBoundingClientRect();
    return { x: e.clientX - rect.left, y: e.clientY - rect.top };
  }

  function strokeColor() {
    return currentTool === "eraser" ? backgroundColor : colorInput.value;
  }

  function drawOneStroke(s) {
    const pts = s.points || [];
    const color = s.color || "#000000";
    const lw = s.lineWidth || 2;
    const shape = s.shapeType || "freehand";
    if (shape !== "freehand" && pts.length >= 2) {
      drawShape(ctx, shape, pts, color, lw);
    } else if (pts.length >= 2) {
      ctx.strokeStyle = color;
      ctx.lineWidth = lw;
      ctx.beginPath();
      ctx.moveTo(pts[0].x, pts[0].y);
      for (let i = 1; i < pts.length; i++) ctx.lineTo(pts[i].x, pts[i].y);
      ctx.stroke();
    }
  }

  function drawShape(context, shape, points, color, lineWidth) {
    context.strokeStyle = color;
    context.lineWidth = lineWidth;
    context.beginPath();
    if (shape === "triangle" && points.length >= 3) {
      context.moveTo(points[0].x, points[0].y);
      context.lineTo(points[1].x, points[1].y);
      context.lineTo(points[2].x, points[2].y);
      context.closePath();
    } else if (points.length < 2) return;
    else {
      const p0 = points[0],
        p1 = points[1];
      const x0 = p0.x,
        y0 = p0.y,
        x1 = p1.x,
        y1 = p1.y;
      if (shape === "line") {
        context.moveTo(x0, y0);
        context.lineTo(x1, y1);
      } else if (shape === "rectangle") {
        const x = Math.min(x0, x1),
          y = Math.min(y0, y1),
          w = Math.abs(x1 - x0),
          h = Math.abs(y1 - y0);
        context.rect(x, y, w, h);
      } else if (shape === "circle") {
        const cx = (x0 + x1) / 2,
          cy = (y0 + y1) / 2;
        const r = Math.sqrt((x1 - x0) ** 2 + (y1 - y0) ** 2) / 2;
        context.arc(cx, cy, Math.max(1, r), 0, Math.PI * 2);
      } else if (shape === "arrow") {
        context.moveTo(x0, y0);
        context.lineTo(x1, y1);
        const angle = Math.atan2(y1 - y0, x1 - x0);
        const headLen = Math.min(15, lineWidth * 4);
        context.moveTo(x1, y1);
        context.lineTo(
          x1 - headLen * Math.cos(angle - 0.4),
          y1 - headLen * Math.sin(angle - 0.4)
        );
        context.moveTo(x1, y1);
        context.lineTo(
          x1 - headLen * Math.cos(angle + 0.4),
          y1 - headLen * Math.sin(angle + 0.4)
        );
      } else if (shape === "diamond") {
        const p0 = points[0],
          p1 = points[1];
        const cx = (p0.x + p1.x) / 2;
        const cy = (p0.y + p1.y) / 2;
        context.moveTo(cx, p0.y);
        context.lineTo(p1.x, cy);
        context.lineTo(cx, p1.y);
        context.lineTo(p0.x, cy);
        context.closePath();
      } else if (shape === "star") {
        const p0 = points[0],
          p1 = points[1];
        const cx = (p0.x + p1.x) / 2;
        const cy = (p0.y + p1.y) / 2;
        const outer = Math.abs(p1.x - p0.x) / 2;
        const inner = outer / 2;
        let rot = (Math.PI / 2) * 3;
        let step = Math.PI / 5;

        context.moveTo(cx, cy - outer);
        for (let i = 0; i < 5; i++) {
          context.lineTo(
            cx + Math.cos(rot) * outer,
            cy + Math.sin(rot) * outer
          );
          rot += step;
          context.lineTo(
            cx + Math.cos(rot) * inner,
            cy + Math.sin(rot) * inner
          );
          rot += step;
        }
        context.lineTo(cx, cy - outer);
        context.closePath();
      } else if (shape === "curveArrow") {
        const p0 = points[0],
          p1 = points[1];
        const cx = (p0.x + p1.x) / 2;
        context.moveTo(p0.x, p0.y);
        context.quadraticCurveTo(cx, p0.y - 80, p1.x, p1.y);

        const angle = Math.atan2(p1.y - p0.y, p1.x - p0.x);
        const headLen = 12;
        context.moveTo(p1.x, p1.y);
        context.lineTo(p1.x - headLen, p1.y - headLen);
        context.moveTo(p1.x, p1.y);
        context.lineTo(p1.x - headLen, p1.y + headLen);
      } else if (shape === "triangle") {
        const cx = (x0 + x1) / 2,
          cy = (y0 + y1) / 2;
        const dx = (x1 - x0) / 2,
          dy = (y1 - y0) / 2;
        context.moveTo(cx - dy, cy + dx);
        context.lineTo(cx + dy, cy - dx);
        context.lineTo(
          (x0 + x1) / 2 - (x1 - x0) / 2,
          (y0 + y1) / 2 - (y1 - y0) / 2
        );
        context.closePath();
      }
    }
    context.stroke();
  }

  function drawAll() {
    const w = canvas.width / (window.devicePixelRatio || 1);
    const h = canvas.height / (window.devicePixelRatio || 1);
    ctx.fillStyle = backgroundColor;
    ctx.fillRect(0, 0, w, h);
    allStrokes.forEach(drawOneStroke);
    if (shapeStart && currentStroke.length >= 1) {
      var pts = [shapeStart].concat(currentStroke);
      drawShape(
        ctx,
        currentTool === "eraser" ? "freehand" : currentTool,
        pts.length >= 2
          ? currentTool === "freehand"
            ? pts
            : [shapeStart, pts[pts.length - 1]]
          : [shapeStart, shapeStart],
        strokeColor(),
        Number(lineWidthInput.value)
      );
    }
  }

  function pointsForShape(tool, start, end) {
    if (
      tool === "line" ||
      tool === "rectangle" ||
      tool === "circle" ||
      tool === "arrow" ||
      tool === "diamond" ||
      tool === "star" ||
      tool === "curveArrow"
    ) {
      return [start, end];
    }
    if (tool === "triangle") {
      const cx = (start.x + end.x) / 2,
        cy = (start.y + end.y) / 2;
      const dx = (end.x - start.x) / 2,
        dy = (end.y - start.y) / 2;
      return [
        { x: cx - dy, y: cy + dx },
        { x: cx + dy, y: cy - dx },
        {
          x: (start.x + end.x) / 2 - (end.x - start.x) / 2,
          y: (start.y + end.y) / 2 - (end.y - start.y) / 2,
        },
      ];
    }
    return currentStroke;
  }

  function sendStroke(shapeType, points) {
    if (!stompClient || !subscribedTopic) return;

    const boardId = boardIdInput.value.trim();
    if (!boardId) return;

    // validation
    if (shapeType === "freehand" && points.length < 2) return;
    if (points.length < 1) return;

    let toSend = points; // default

    // Shapes that use drag start + end
    if (
      shapeType === "line" ||
      shapeType === "rectangle" ||
      shapeType === "circle" ||
      shapeType === "arrow" ||
      shapeType === "diamond" ||
      shapeType === "star" ||
      shapeType === "curveArrow"
    ) {
      toSend = points;
    }

    // Triangle special case
    else if (shapeType === "triangle" && points.length === 3) {
      toSend = points;
    }

    // Freehand + eraser
    else if (shapeType === "freehand" || shapeType === "eraser") {
      toSend = points;
    }

    stompClient.publish({
      destination: "/app/whiteboard/" + boardId + "/stroke",
      body: JSON.stringify({
        color: strokeColor(),
        lineWidth: Number(lineWidthInput.value),
        shapeType: shapeType === "eraser" ? "freehand" : shapeType,
        points: toSend,
        authorId: authorId,
      }),
    });
  }

  function startDrawing(x, y) {
    isDrawing = true;

    if (isFreehandTool()) {
      ctx.save();
      ctx.globalCompositeOperation = "source-over";

      ctx.strokeStyle = isEraser() ? backgroundColor : colorInput.value;

      ctx.lineWidth = Number(lineWidthInput.value) * (isEraser() ? 2 : 1);

      ctx.beginPath();
      ctx.moveTo(x, y);
      currentStroke = [{ x, y }];
    } else {
      shapeStart = { x, y };
      currentStroke = [];
    }
  }

  function addPoint(x, y) {
    if (!isDrawing) return;

    if (isFreehandTool()) {
      ctx.lineTo(x, y);
      ctx.stroke();
      currentStroke.push({ x, y });
    } else {
      currentStroke = [{ x, y }]; // shape preview end
      drawAll(); // redraw board + preview
    }
  }

  function endDrawing() {
    if (!isDrawing) return;
    isDrawing = false;

    if (isFreehandTool()) {
      ctx.closePath();
      ctx.restore();

      if (currentStroke.length >= 2) {
        // Do NOT push a local stroke without an id; let the server echo
        // back a StrokeDto with an id via STROKE_ADDED, and we add that.
        sendStroke("freehand", currentStroke);
      }

      currentStroke = [];
    } else {
      if (shapeStart && currentStroke.length) {
        const end = currentStroke[0];
        const pts =
          currentTool === "triangle"
            ? pointsForShape("triangle", shapeStart, end)
            : [shapeStart, end];

        // Same here: only send to the server; we will append
        // the authoritative stroke (with id) when STROKE_ADDED arrives.
        sendStroke(currentTool, pts);
      }

      shapeStart = null;
      currentStroke = [];
      drawAll();
    }
  }

  // ========by my=======
  canvas.addEventListener("mousedown", function (e) {
    e.preventDefault();
    startDrawing(getCanvasPoint(e).x, getCanvasPoint(e).y);
  });
  canvas.addEventListener("mousemove", function (e) {
    addPoint(getCanvasPoint(e).x, getCanvasPoint(e).y);
  });
  canvas.addEventListener("mouseup", endDrawing);
  canvas.addEventListener("mouseleave", endDrawing);
  canvas.addEventListener(
    "touchstart",
    function (e) {
      e.preventDefault();
      var t = e.touches[0];
      var p = getCanvasPoint(t);
      startDrawing(p.x, p.y);
    },
    { passive: false }
  );
  canvas.addEventListener(
    "touchmove",
    function (e) {
      e.preventDefault();
      addPoint(getCanvasPoint(e.touches[0]).x, getCanvasPoint(e.touches[0]).y);
    },
    { passive: false }
  );
  canvas.addEventListener(
    "touchend",
    function (e) {
      e.preventDefault();
      endDrawing();
    },
    { passive: false }
  );

  function setStatus(connected) {
    statusEl.textContent = connected ? "Connected" : "Disconnected";
    statusEl.className = connected ? "connected" : "disconnected";
  }

  function doConnect(boardId) {
    if (stompClient && stompClient.connected) {
      if (subscribedTopic) stompClient.unsubscribe(subscribedTopic);
      stompClient.deactivate();
      stompClient = null;
      setStatus(false);
    }
    var socket = new SockJS(window.location.origin + "/ws");
    stompClient = new StompJs.Client({
      webSocketFactory: function () {
        return socket;
      },
    });
    stompClient.onConnect = function () {
      setStatus(true);
      subscribedTopic = stompClient.subscribe(
        "/topic/whiteboard/" + boardId,
        function (msg) {
          try {
            var body = JSON.parse(msg.body);
            if (body.type === "STROKE_ADDED" && body.payload) {
              allStrokes.push(body.payload);
              drawAll();
            } else if (body.type === "STROKE_REMOVED" && body.payload) {
              var id =
                typeof body.payload === "string"
                  ? body.payload
                  : body.payload.id;
              allStrokes = allStrokes.filter(function (s) {
                return s.id !== id;
              });
              drawAll();
            } else if (body.type === "BOARD_CLEARED") {
              allStrokes = [];
              drawAll();
            }
          } catch (err) {
            console.error(err);
          }
        }
      );
      fetch(window.location.origin + "/api/whiteboards/" + boardId)
        .then(function (r) {
          return r.ok ? r.json() : null;
        })
        .then(function (board) {
          if (board && board.strokes) {
            allStrokes = board.strokes;
            drawAll();
          }
        })
        .catch(function (err) {
          console.error(err);
        });
    };
    stompClient.onStompError = function (frame) {
      console.error(frame);
      setStatus(false);
    };
    stompClient.onWebSocketClose = function () {
      setStatus(false);
    };
    stompClient.activate();
  }

  document
    .getElementById("createBoardBtn")
    .addEventListener("click", async function () {
      try {
        var res = await fetch(window.location.origin + "/api/whiteboards", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ name: "My Whiteboard" }),
        });
        if (!res.ok) throw new Error("Failed: " + res.status);
        var board = await res.json();
        boardIdInput.value = board.id;
        doConnect(board.id);
      } catch (err) {
        alert("Server not running? " + err.message);
      }
    });

  connectBtn.addEventListener("click", function () {
    var boardId = boardIdInput.value.trim();
    if (!boardId) {
      alert("Enter Board ID or click New board");
      return;
    }
    if (stompClient && stompClient.connected) {
      if (subscribedTopic) stompClient.unsubscribe(subscribedTopic);
      stompClient.deactivate();
      stompClient = null;
      setStatus(false);
      return;
    }
    doConnect(boardId);
  });

  document.getElementById("undoBtn").addEventListener("click", function () {
    if (allStrokes.length === 0 || !stompClient || !stompClient.connected)
      return;
    var boardId = boardIdInput.value.trim();
    if (!boardId) return;

    // Find the most recent stroke that actually has a server id.
    var idx = allStrokes.length - 1;
    while (idx >= 0 && !allStrokes[idx].id) {
      idx--;
    }
    if (idx < 0) return;

    var last = allStrokes[idx];
    stompClient.publish({
      destination: "/app/whiteboard/" + boardId + "/stroke/remove",
      body: JSON.stringify({ strokeId: last.id }),
    });
  });

  document.getElementById("clearBtn").addEventListener("click", function () {
    if (!stompClient || !stompClient.connected) return;
    var boardId = boardIdInput.value.trim();
    if (!boardId) return;
    if (!confirm("Clear entire board?")) return;
    stompClient.publish({
      destination: "/app/whiteboard/" + boardId + "/clear",
      body: JSON.stringify({}),
    });
  });

  var params = new URLSearchParams(window.location.search);
  if (params.get("board")) boardIdInput.value = params.get("board");
})();
