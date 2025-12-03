document.addEventListener("DOMContentLoaded", () => {
  const page = document.body.getAttribute("data-page")

  const learnersKey = "sdg13_learners"
  let learners = localStorage.getItem(learnersKey)
  learners = learners ? Number.parseInt(learners) + 1 : 1
  localStorage.setItem(learnersKey, learners)

  const learnersDisplay = document.getElementById("learners-count")
  if (learnersDisplay) learnersDisplay.textContent = learners

  const exploreBtn = document.getElementById("explore-features-btn")
  if (exploreBtn) {
    exploreBtn.addEventListener("click", () => {
      window.location.href = "features.html"
    })
  }

  const darkToggle = document.getElementById("dark-toggle")
  const darkKey = "sdg13_dark_mode"

  if (localStorage.getItem(darkKey) === "on") {
    document.body.classList.add("dark")
    if (darkToggle) darkToggle.textContent = "☀ Light"
  }

  if (darkToggle) {
    darkToggle.addEventListener("click", () => {
      document.body.classList.toggle("dark")
      const isDark = document.body.classList.contains("dark")
      localStorage.setItem(darkKey, isDark ? "on" : "off")
      darkToggle.textContent = isDark ? "☀ Light" : "🌙 Dark"
    })
  }

  const fadeSections = document.querySelectorAll(".fade-section")
  if ("IntersectionObserver" in window) {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add("visible")
            observer.unobserve(entry.target)
          }
        })
      },
      { threshold: 0.15 },
    )

    fadeSections.forEach((el) => observer.observe(el))
  } else {
    fadeSections.forEach((el) => el.classList.add("visible"))
  }

  if (page === "facts") {
    const factsContainer = document.getElementById("facts-container")
    const newFactsBtn = document.getElementById("new-facts")

    const climateFacts = [
      "The last 8 years were the warmest ever recorded.",
      "Carbon dioxide levels are the highest in 2 million years.",
      "Arctic sea ice is shrinking at 13% per decade.",
      "Sea levels have risen 20 cm since 1900.",
      "Over 1 million species are at risk of extinction.",
      "The ocean absorbs 90% of excess heat from global warming.",
      "Deforestation contributes up to 15% of global emissions.",
      "The hottest temperature ever recorded on Earth was 56.7°C.",
      "Livestock accounts for 14% of greenhouse gas emissions.",
      "Climate change increases the spread of diseases.",
      "Antarctica loses 150 billion tons of ice yearly.",
      "Transportation contributes 28% of CO₂ emissions.",
      "Renewable energy could power the entire world by 2050.",
      "Air pollution kills 7 million people annually.",
      "Extreme heat affects 1 in 3 people worldwide.",
      "Coral reefs could disappear by 2050.",
      "Plastic pollution kills 1 million seabirds every year.",
      "Wildfires release billions of tons of CO₂ annually.",
      "Oceans are 30% more acidic than 200 years ago.",
      "Methane is 80× more powerful at trapping heat than CO₂.",
    ]

    function getRandomFacts(count) {
      const shuffled = climateFacts.sort(() => Math.random() - 0.5)
      return shuffled.slice(0, count)
    }

    function renderFacts() {
      factsContainer.innerHTML = ""
      const selectedFacts = getRandomFacts(12)

      selectedFacts.forEach((fact) => {
        const div = document.createElement("div")
        div.className = "fact-card"
        div.innerHTML = `<p>${fact}</p>`
        factsContainer.appendChild(div)
      })
    }

    renderFacts()

    newFactsBtn.addEventListener("click", renderFacts)
  }

  if (page === "quiz") {
    const quizForm = document.getElementById("quiz-form")
    const quizResult = document.getElementById("quiz-result")
    const leaderScore = document.getElementById("leader-score")

    quizForm.addEventListener("submit", (e) => {
      e.preventDefault()

      let score = 0
      const answers = { q1: "b", q2: "b", q3: "b" }

      Object.keys(answers).forEach((q) => {
        const selected = quizForm.querySelector(`input[name="${q}"]:checked`)
        if (selected && selected.value === answers[q]) score++
      })

      const finalScore = score * 10
      quizResult.textContent = "Your Score: " + finalScore

      if (leaderScore) leaderScore.value = finalScore
    })
  }

  if (page === "game") {
    let time = 10
    let score = 0
    let running = false
    let timerId = null

    const timeLeft = document.getElementById("time-left")
    const scoreDisplay = document.getElementById("game-score")
    const startGame = document.getElementById("start-game")
    const leafBtn = document.getElementById("leaf-button")
    const gameResult = document.getElementById("game-result")
    const leaderScore = document.getElementById("leader-score")

    startGame.addEventListener("click", () => {
      time = 10
      score = 0
      running = true
      timeLeft.textContent = time
      scoreDisplay.textContent = score
      leafBtn.disabled = false
      gameResult.textContent = ""
      if (timerId) clearInterval(timerId)

      timerId = setInterval(() => {
        time--
        timeLeft.textContent = time

        if (time <= 0) {
          clearInterval(timerId)
          running = false
          leafBtn.disabled = true
          gameResult.textContent = "Your Score: " + score
          if (leaderScore) leaderScore.value = score
        }
      }, 1000)
    })

    leafBtn.addEventListener("click", () => {
      if (running) {
        score++
        scoreDisplay.textContent = score
      }
    })
  }

  if (page === "leaderboard") {
    const list = document.getElementById("leaderboard-list")
    const form = document.getElementById("leader-form")
    const clearBtn = document.getElementById("clear-leaderboard")
    const key = "sdg13_leaderboard"

    function load() {
      return JSON.parse(localStorage.getItem(key) || "[]")
    }

    function save(data) {
      localStorage.setItem(key, JSON.stringify(data))
    }

    function render() {
      const data = load().sort((a, b) => b.score - a.score)
      list.innerHTML = ""
      if (!data.length) {
        const li = document.createElement("li")
        li.textContent = "No scores yet. Play the game or quiz to add your name!"
        list.appendChild(li)
        return
      }
      data.forEach((item) => {
        const li = document.createElement("li")
        li.textContent = `${item.name} – ${item.score}`
        list.appendChild(li)
      })
    }

    form.addEventListener("submit", (e) => {
      e.preventDefault()
      const name = document.getElementById("leader-name").value.trim()
      const score = Number.parseInt(document.getElementById("leader-score").value)

      if (!name || !score || score <= 0) return

      const data = load()
      data.push({ name, score })
      save(data)
      render()
    })

    clearBtn.addEventListener("click", () => {
      localStorage.removeItem(key)
      render()
    })

    render()
  }
})
