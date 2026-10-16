# GradeFlow: Grade Processing System

A Java web app (Servlets + JSP) that reads a class roster of scores and creates a grade report.

## Features

- **Batch Processing:** reads the whole class list one student at a time (loops)
- **Grade Evaluation:** turns the average into a grade, 1.00 to 5.00 / A+ to F (if / else if)
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

See the **Sample CSV** page in the app, or `sample-roster.csv`.

## Grading

Average = Quiz 20% + Assignment 20% + Midterm 25% + Final Exam 35%

| Grade | Percent | Letter | Description |
|---|---|---|---|
| 1.00 | 98 - 100 | A+ | Excellent |
| 1.25 | 95 - 97 | A | Excellent |
| 1.50 | 92 - 94 | A- | Very Good |
| 1.75 | 89 - 91 | B+ | Very Good |
| 2.00 | 86 - 88 | B | Good |
| 2.25 | 83 - 85 | B- | Good |
| 2.50 | 80 - 82 | C+ | Fair |
| 2.75 | 77 - 79 | C | Passed |
| 3.00 | 75 - 76 | C- | Passed |
| 4.00 | 70 - 74 | D | Conditional |
| 5.00 | Below 70 | F | Failed |

## Main Files

| File | Purpose |
|---|---|
| `RosterParser.java` | Reads and checks the roster |
| `GradeEvaluator.java` | Computes the average and grade |
| `StatusRouter.java` | Assigns description, status, honors, remediation |
| `ReportGenerator.java` | Builds the class summary |
| `ProcessServlet.java` | Runs everything when the form is submitted |
| `index.jsp`, `results.jsp`, `sample.jsp` | The web pages |

`Dockerfile` is only used for hosting the app online (Render). Eclipse doesn't need it.

## Team

| Member | Role |
|---|---|
| Member 1 | |
| Member 2 | |
| Member 3 | |
| Member 4 | |
| Member 5 | |
