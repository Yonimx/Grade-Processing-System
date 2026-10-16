# GradeFlow: Grade Processing System

A Java web app (Servlets + JSP) that reads a class roster of scores and creates a grade report.

## Features

- **Batch Processing:** reads the whole class list one student at a time (loops)
- **Grade Evaluation:** turns the average into a letter grade, A+ to F (if / else if)
- **Status Routing:** gives each student a remark, status, honors and remediation flag (switch)
- **Report:** class average, pass rate, rankings, honor roll, and a CSV download

## How to Run (Eclipse)

1. **File → Import → Existing Projects into Workspace**, then select this folder
2. Right-click the project → **Run As → Run on Server** → **Tomcat v11.0**
3. Open `http://localhost:8080/GradeProcessingSystem/`
4. Click **Load sample data** → **Process Grades**

Requires Java 21 and Apache Tomcat 11.

## Input Format

```
Student ID, Name, Quiz, Assignment, Midterm, Final Exam
2024-0001, Juan Dela Cruz, 88, 92, 85, 90
```

## Grading

Average = Quiz 20% + Assignment 20% + Midterm 25% + Final Exam 35%

| Grade | Average | Status |
|---|---|---|
| A+ / A / A- | 90 - 100 | Good Standing (can get honors) |
| B+ / B / B- | 80 - 89 | Good Standing |
| C+ / C | 73 - 79 | Good Standing |
| C- | 70 - 72 | Academic Warning |
| D | 60 - 69 | Probation |
| F | below 60 | Failed |

## Main Files

| File | Purpose |
|---|---|
| `RosterParser.java` | Reads and checks the roster |
| `GradeEvaluator.java` | Computes average and letter grade |
| `StatusRouter.java` | Assigns remarks, status, honors |
| `ReportGenerator.java` | Builds the class summary |
| `ProcessServlet.java` | Runs everything when the form is submitted |
| `index.jsp`, `results.jsp`, `about.jsp` | The web pages |

`Dockerfile` is only used for hosting the app online (Render). Eclipse doesn't need it.

## Team

| Member | Role |
|---|---|
| Member 1 | |
| Member 2 | |
| Member 3 | |
| Member 4 | |
| Member 5 | |
