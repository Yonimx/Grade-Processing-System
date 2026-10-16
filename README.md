# GradeFlow: Academic Grade Processing System

A Java web application (Servlets + JSP) that reads a class roster of raw scores, runs every student
through a four-stage grading pipeline, and produces a class performance report.

**Built for:** Eclipse IDE for Enterprise Java, Apache Tomcat 11, Java 21

---

## How to run it in Eclipse

1. **Import the project**
   `File > Import... > General > Existing Projects into Workspace > Next`
   Click **Browse** for *Select root directory* and choose the `GradeProcessingSystem` folder.
   Tick **Copy projects into workspace**, then click **Finish**.

2. **Check the server runtime** (only needed if you see red errors on the `jakarta.servlet` imports)
   Right-click the project, then `Properties > Targeted Runtimes`, and tick **Apache Tomcat v11.0**.

3. **Run it**
   Right-click the project, then `Run As > Run on Server`. Pick **Tomcat v11.0 Server at localhost** and click **Finish**.

4. Open **http://localhost:8080/GradeProcessingSystem/**
   Click **Load sample data**, then **Process Grades**.

> If port 8080 is already in use, stop the other server in the **Servers** tab first.

---

## Features (how the project brief maps to the code)

| Requirement | What the app does | Where in the code |
|---|---|---|
| **Iterative Batch Processing** | A `for` loop reads the roster line by line, validates each record and skips bad lines with a message | `service/RosterParser.java` |
| **Conditional Evaluation** | Weighted average, then an `if / else if` ladder of 11 thresholds gives the letter grade (A+ to F) | `service/GradeEvaluator.java` |
| **State Routing** | `switch` statements on the letter grade assign the remark, academic status, honors and remediation flag | `service/StatusRouter.java` |
| **Report Generation** | Class average, median, highest/lowest, pass rate, ranks, grade distribution, honor roll, remediation list, CSV export | `service/ReportGenerator.java`, `servlet/ExportServlet.java` |

### Grading rules

* **Weights:** Quiz 20%, Assignment 20%, Midterm 25%, Final Exam 35%
* **Letter grades:** A+ 97+, A 93+, A- 90+, B+ 87+, B 83+, B- 80+, C+ 77+, C 73+, C- 70+, D 60+, F below 60
* **Status:** A+ to C is *Good Standing*, C- is *Academic Warning*, D is *Probation*, F is *Failed*
* **Honors:** A+ *Highest Honors*, A *High Honors*, A- *Honors*. Honors are withheld if any component is below 85.
* **Remediation:** C- *Tutorial sessions*, D *Remedial program*, F *Must retake*. Passing students with any component below 70 get *Review weak area*.

---

## Input format

One student per line (a header line is optional):

```
Student ID, Name, Quiz, Assignment, Midterm, Final Exam
2024-0001, Andrea Santos, 98, 99, 97, 98
2024-0017, "Dela Cruz, Juan", 78, 80, 76, 79
```

You can paste the lines into the text box or upload a `.csv` file (for example, one saved from Excel).
Invalid lines are skipped and listed on the results page. A line is invalid if it has missing values,
a score that is not a number, a score outside 0 to 100, or a duplicate ID.

---

## Project structure

```
GradeProcessingSystem/
├── src/main/java/com/grading/
│   ├── model/
│   │   ├── Student.java            one student's scores and results
│   │   └── ClassReport.java        class-wide summary
│   ├── service/                    the 4-stage pipeline
│   │   ├── RosterParser.java       Stage 1: loops
│   │   ├── GradeEvaluator.java     Stage 2: if / else-if thresholds
│   │   ├── StatusRouter.java       Stage 3: switch logic
│   │   └── ReportGenerator.java    Stage 4: class metrics
│   ├── servlet/
│   │   ├── ProcessServlet.java     POST /process: runs the pipeline
│   │   └── ExportServlet.java      GET /export: downloads the CSV report
│   └── util/
│       └── HtmlUtil.java           HTML escaping and number formatting for the JSPs
└── src/main/webapp/
    ├── index.jsp                   roster input form
    ├── results.jsp                 class report
    ├── about.jsp                   "How It Works": pipeline, grading policy, team
    ├── sample-roster.csv           17 sample students covering every grade
    ├── css/style.css
    └── WEB-INF/
        ├── web.xml
        └── includes/               shared nav bar and footer
```

**Flow (MVC):** `index.jsp` sends the form to `ProcessServlet`, which runs Parser, Evaluator, Router and
ReportGenerator in that order. It stores the results in the session and redirects to `results.jsp`.
From there, `ExportServlet` downloads the CSV.

---

## Suggested split for the group

| Member | Part |
|---|---|
| Member 1 | Project lead / integration: `ProcessServlet`, `ExportServlet`, `web.xml` |
| Member 2 | Stage 1: `RosterParser`, `Student` |
| Member 3 | Stages 2 and 3: `GradeEvaluator`, `StatusRouter` |
| Member 4 | Stage 4: `ReportGenerator`, `ClassReport` |
| Member 5 | UI: JSP pages and `style.css` |

Put your names in the **Project Team** table at the bottom of `about.jsp`.

> **If you change the grade thresholds** in `GradeEvaluator.java`, also update the `ranges` array in
> `index.jsp` and `about.jsp`. Those arrays only label the ranges on screen.
