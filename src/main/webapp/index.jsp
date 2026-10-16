<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	import="java.util.List, com.grading.util.HtmlUtil, com.grading.service.GradeEvaluator, com.grading.service.StatusRouter"%>
<%
	// Filled in by ProcessServlet when the roster could not be processed
	List<?> errors = (List<?>) request.getAttribute("errors");
	String course = (String) request.getAttribute("course");
	String roster = (String) request.getAttribute("roster");

	// The school's grading system shown in the side panel
	String[] letters = GradeEvaluator.LETTER_GRADES;
	String[] grades = GradeEvaluator.NUMERIC_GRADES;
	String[] ranges = GradeEvaluator.PERCENT_RANGES;
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>GradeFlow - Process Grades</title>
	<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/includes/nav.jspf" %>

<main class="container">

	<section class="hero">
		<h1>Automated Grade Processing</h1>
		<p>Paste or upload a class roster with raw scores. GradeFlow computes every student's weighted
			average, assigns a grade (1.00 - 5.00), attaches academic status, honors and remediation flags,
			and compiles a complete class report.</p>
		<div class="pipeline">
			<span class="step"><b>1</b>Ingest roster</span><span class="arrow">&rarr;</span>
			<span class="step"><b>2</b>Evaluate grades</span><span class="arrow">&rarr;</span>
			<span class="step"><b>3</b>Route statuses</span><span class="arrow">&rarr;</span>
			<span class="step"><b>4</b>Generate report</span>
		</div>
	</section>

	<% if (request.getParameter("empty") != null) { %>
	<div class="alert alert-info">There are no results yet. Process a class roster first.</div>
	<% } %>

	<% if (errors != null && !errors.isEmpty()) { %>
	<div class="alert alert-error">
		<strong>The roster could not be processed.</strong>
		<ul>
			<% for (Object error : errors) { %>
			<li><%= HtmlUtil.esc(error.toString()) %></li>
			<% } %>
		</ul>
	</div>
	<% } %>

	<div class="grid-main">

		<!-- Roster input form -->
		<div class="card">
			<h2>Class Roster</h2>
			<form id="rosterForm" action="<%= request.getContextPath() %>/process" method="post" enctype="multipart/form-data">

				<div class="field">
					<label for="course">Course / Section</label>
					<input type="text" id="course" name="course" maxlength="80"
						placeholder="e.g. IT 201 - Section A" value="<%= HtmlUtil.esc(course) %>">
				</div>

				<div class="field">
					<div class="label-row">
						<label for="roster">Student Scores</label>
						<span class="btn-group">
							<button type="button" class="btn btn-small" id="loadSample">Load sample data</button>
							<button type="button" class="btn btn-small" id="clearRoster">Clear</button>
						</span>
					</div>
					<textarea id="roster" name="roster" rows="14"
						placeholder="Student ID, Name, Quiz, Assignment, Midterm, Final Exam&#10;2024-0001, Juan Dela Cruz, 88, 92, 85, 90&#10;2024-0002, Maria Santos, 95, 90, 93, 96"><%= HtmlUtil.esc(roster) %></textarea>
					<p class="hint">One student per line: <code>ID, Name, Quiz, Assignment, Midterm, Final Exam</code>.
						Scores must be from 0 to 100. A header line is optional.</p>
				</div>

				<div class="divider">or upload a CSV file</div>

				<div class="field">
					<input type="file" id="rosterFile" name="rosterFile" accept=".csv,.txt">
					<p class="hint">If a file is chosen it is used instead of the text box.
						See the <a href="<%= request.getContextPath() %>/sample.jsp">Sample CSV</a> page for the format.</p>
				</div>

				<button type="submit" class="btn btn-primary btn-block">Process Grades</button>
			</form>
		</div>

		<!-- Grading scheme summary -->
		<div>
			<div class="card">
				<h2>Score Weights</h2>
				<table class="simple-table">
					<tr><td>Quizzes</td><td class="num"><%= Math.round(GradeEvaluator.QUIZ_WEIGHT * 100) %>%</td></tr>
					<tr><td>Assignments</td><td class="num"><%= Math.round(GradeEvaluator.ASSIGNMENT_WEIGHT * 100) %>%</td></tr>
					<tr><td>Midterm Exam</td><td class="num"><%= Math.round(GradeEvaluator.MIDTERM_WEIGHT * 100) %>%</td></tr>
					<tr><td>Final Exam</td><td class="num"><%= Math.round(GradeEvaluator.FINAL_WEIGHT * 100) %>%</td></tr>
				</table>
			</div>

			<div class="card">
				<h2>Grading System</h2>
				<table class="simple-table">
					<tr><th>Grade</th><th>Percent</th><th>Letter</th><th>Description</th></tr>
					<% for (int i = 0; i < letters.length; i++) { %>
					<tr>
						<td><strong><%= grades[i] %></strong></td>
						<td class="nowrap"><%= ranges[i] %></td>
						<td><span class="badge <%= HtmlUtil.gradeClass(letters[i]) %>"><%= letters[i] %></span></td>
						<td class="muted"><%= StatusRouter.remarkFor(letters[i]) %></td>
					</tr>
					<% } %>
				</table>
				<p class="hint">Passing grade: 3.00 (75%).</p>
			</div>
		</div>
	</div>
</main>

<%@ include file="/WEB-INF/includes/footer.jspf" %>

<script>
	var rosterBox = document.getElementById("roster");
	var courseBox = document.getElementById("course");
	var fileInput = document.getElementById("rosterFile");

	function loadSample() {
		fetch("<%= request.getContextPath() %>/sample-roster.csv")
			.then(function (res) { return res.text(); })
			.then(function (text) {
				rosterBox.value = text.trim();
				if (courseBox.value.trim() === "") {
					courseBox.value = "IT 201 - Section A";
				}
			});
	}
	document.getElementById("loadSample").addEventListener("click", loadSample);

	// "Try It With This Sample" on the Sample CSV page opens this page with ?sample=1
	if (new URLSearchParams(window.location.search).get("sample") === "1") {
		loadSample();
	}

	document.getElementById("clearRoster").addEventListener("click", function () {
		rosterBox.value = "";
		fileInput.value = "";
	});

	document.getElementById("rosterForm").addEventListener("submit", function (e) {
		if (rosterBox.value.trim() === "" && fileInput.files.length === 0) {
			e.preventDefault();
			alert("Please enter the student scores or choose a CSV file first.");
		}
	});
</script>
</body>
</html>
