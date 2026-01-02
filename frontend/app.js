const API = "http://localhost:8080/tasks";

/* ------- TAB SYSTEM ------- */
function showTab(id){
  document.querySelectorAll(".tab").forEach(t=>t.classList.remove("active"));
  document.querySelectorAll(".tabBtn").forEach(t=>t.classList.remove("active"));
  
  document.getElementById(id).classList.add("active");
  event.target.classList.add("active");
}

/* ------- LOAD TASKS ------- */
async function load(){
  const res = await fetch(API);
  const tasks = await res.json();

  const list = document.getElementById("list");
  list.innerHTML = "";

  let completed = 0;
  tasks.forEach(t=>{
    if(t.completed) completed++;

    const div = document.createElement("div");
    div.className = "taskBox";
    div.innerHTML = `
      <h3>${t.title}</h3>
      <p>Category: ${t.category}</p>
      <p>Priority: ${t.priority}</p>
      <p>Due: ${t.dueDate}</p>
      <p>Status: ${t.completed ? "Completed" : "Pending"}</p>
      ${!t.completed ? `<button onclick="completeTask(${t.id})">Complete</button>` : ""}
    `;
    list.appendChild(div);
  });

  drawCharts(tasks, completed);
  insights(tasks);
}

/* -------- ADD TASK ------- */
async function addTask(){
  const title = taskInput.value;
  const category = category.value;
  const priority = priority.value;
  const due = dueDate.value;
  const time = time.value;

  if(!title || !due) return alert("Enter task + date");

  await fetch(API,{
    method:"POST",
    headers:{ "Content-Type":"application/json" },
    body:JSON.stringify({ title, category, priority, dueDate:due, estimate:time })
  });

  taskInput.value = "";
  load();
}

/* -------- COMPLETE ------- */
async function completeTask(id){
  await fetch(`${API}/${id}/complete`, { method:"POST" });
  load();
}

/* -------- CHARTS ------- */
let pieChart, barChart;

function drawCharts(tasks, completed){
  const pending = tasks.length - completed;

  if(pieChart) pieChart.destroy();
  pieChart = new Chart(pieChart, {
    type:"pie",
    data:{ labels:["Completed","Pending"], datasets:[{ data:[completed,pending],
    backgroundColor:["#4cff88","#ff4757"]}]}
  });

  if(barChart) barChart.destroy();
  barChart = new Chart(barChart,{
    type:"bar",
    data:{ labels:["Completed","Pending"],
    datasets:[{ data:[completed,pending], backgroundColor:["#4cff88","#ff4757"] }]}
  });

  updateRisk(tasks,pending);
}

/* -------- RISK ------- */
function updateRisk(tasks,pending){
  const box = riskBox;

  if(tasks.length===0){
    box.innerHTML="Risk Status: No Data";
    box.style.color="white";
    return;
  }

  let risk="SAFE", color="lightgreen";

  if(pending > tasks.length/2){ risk="AT RISK"; color="red"; }
  else if(pending>0){ risk="MODERATE"; color="yellow"; }

  box.innerHTML = `Risk Status: ${risk}`;
  box.style.color=color;
}

/* ------- INSIGHTS ------- */
function insights(tasks){
  const text = document.getElementById("insightText");

  if(tasks.length === 0){
    text.innerHTML="No data yet. Start adding tasks 💡";
    return;
  }

  const pending = tasks.filter(t=>!t.completed).length;

  if(pending === 0) text.innerHTML="🔥 Perfect! All tasks done!";
  else if(pending <= 2) text.innerHTML="👍 Good control. Keep pace!";
  else text.innerHTML="⚠️ Too many pending. Focus up!";
}

load();
