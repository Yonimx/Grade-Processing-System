<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	import="java.util.List, java.util.Map, com.grading.model.Student, com.grading.model.ClassReport, com.grading.util.HtmlUtil, com.grading.service.GradeEvaluator"%>
<%
	// Results are stored in the session by ProcessServlet
	@SuppressWarnings("unchecked")
	List<Student> students = (List<Student>) session.getAttribute("students");
	ClassReport report = (ClassReport) session.getAttribute("report");
	List<?> skipped = (List<?>) session.getAttribute("skippedLines");

	if (students == null || report == null) {
		response.sendRedirect(request.getContextPath() + "/index.jsp?empty=1");
		return;
	}

	String classLetter = GradeEvaluator.toLetterGrade(report.getClassAverage());
	String classGrade = GradeEvaluator.toNumericGrade(classLetter);
	Map<String, Integer> statusCounts = report.getStatusCounts();
	int conditionalCount = statusCounts.getOrDefault("Conditional", 0);
	int failedCount = statusCounts.getOrDefault("Failed", 0);
	int warningCount = statusCounts.getOrDefault("Academic Warning", 0);

	// largest count, used to scale the bar charts
	int maxGradeCount = 1;
	for (int count : report.getGradeDistribution().values()) {
		maxGradeCount = Math.max(maxGradeCount, count);
	}
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>GradeFlow - Results for <%= HtmlUtil.esc(report.getCourseName()) %></title>
	<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/includes/nav.jspf" %>

<main class="container">

	<section class="report-head">
		<div>
			<p class="eyebrow">Class Grade Report</p>
			<h1><%= HtmlUtil.esc(report.getCourseName()) %></h1>
			<span class="muted">Generated <%= report.getGeneratedOn() %> &middot; <%= report.getTotalStudents() %> students processed</span>
		</div>
		<div class="btn-group no-print">
			<a class="btn" href="<%= request.getContextPath() %>/export">Download CSV</a>
			<button type="button" class="btn" onclick="window.print()">Print</button>
			<a class="btn btn-primary" href="<%= request.getContextPath() %>/index.jsp">Process New Roster</a>
		</div>
	</section>

	<% if (skipped != null && !skipped.isEmpty()) { %>
	<div class="alert alert-warning">
		<strong><%= skipped.size() %> line(s) were skipped and are not part of this report:</strong>
		<ul>
			<% for (Object line : skipped) { %>
			<li><%= HtmlUtil.esc(line.toString()) %></li>
			<% } %>
		</ul>
	</div>
	<% } %>

	<!-- ===== Class metrics ===== -->
	<section class="stats">
		<div class="stat">
			<div class="label">Total Students</div>
			<div class="value"><%= report.getTotalStudents() %></div>
			<div class="sub"><%= report.getPassedCount() %> passed &middot; <%= conditionalCount %> conditional &middot; <%= failedCount %> failed</div>
		</div>
		<div class="stat">
			<div class="label">Class Average</div>
			<div class="value"><%= HtmlUtil.fmt(report.getClassAverage()) %></div>
			<div class="sub">Equivalent grade <strong><%= classGrade %></strong> <span class="badge <%= HtmlUtil.gradeClass(classLetter) %>"><%= classLetter %></span></div>
		</div>
		<div class="stat">
			<div class="label">Pass Rate</div>
			<div class="value"><%= HtmlUtil.fmt(report.getPassRate()) %>%</div>
			<div class="sub">Median average <%= HtmlUtil.fmt(report.getMedian()) %></div>
		</div>
		<div class="stat">
			<div class="label">Honor Roll</div>
			<div class="value"><%= report.getHonorRoll().size() %></div>
			<div class="sub">students with honors</div>
		</div>
		<div class="stat">
			<div class="label">Highest Average</div>
			<div class="value"><%= HtmlUtil.fmt(report.getHighestAverage()) %></div>
			<div class="sub"><%= HtmlUtil.esc(report.getHighestName()) %></div>
		</div>
		<div class="stat">
			<div class="label">Lowest Average</div>
			<div class="value"><%= HtmlUtil.fmt(report.getLowestAverage()) %></div>
			<div class="sub"><%= HtmlUtil.esc(report.getLowestName()) %></div>
		</div>
		<div class="stat">
			<div class="label">Needs Remediation</div>
			<div class="value"><%= report.getRemediationList().size() %></div>
			<div class="sub">students flagged</div>
		</div>
		<div class="stat">
			<div class="label">Conditional / Failed</div>
			<div class="value"><%= conditionalCount + failedCount %></div>
			<div class="sub"><%= warningCount %> on academic warning</div>
		</div>
	</section>

	<!-- ===== Charts ===== -->
	<section class="grid-2">
		<div class="card" style="margin-bottom:0">
			<h2>Grade Distribution</h2>
			<% for (Map.Entry<String, Integer> entry : report.getGradeDistribution().entrySet()) {
				int width = entry.getValue() * 100 / maxGradeCount; %>
			<div class="bar-row">
				<span><strong><%= GradeEvaluator.toNumericGrade(entry.getKey()) %></strong>
					<span class="badge <%= HtmlUtil.gradeClass(entry.getKey()) %>"><%= entry.getKey() %></span></span>
				<div class="bar-track"><div class="bar-fill <%= HtmlUtil.gradeClass(entry.getKey()) %>" style="width:<%= width %>%"></div></div>
				<span class="bar-count"><%= entry.getValue() %></span>
			</div>
			<% } %>
		</div>

		<div class="card" style="margin-bottom:0">
			<h2>Academic Status</h2>
			<% for (Map.Entry<String, Integer> entry : report.getStatusCounts().entrySet()) {
				int width = entry.getValue() * 100 / report.getTotalStudents(); %>
			<div class="bar-row" style="grid-template-columns:150px 1fr 36px">
				<span><span class="badge <%= HtmlUtil.statusClass(entry.getKey()) %>"><%= entry.getKey() %></span></span>
				<div class="bar-track"><div class="bar-fill" style="width:<%= width %>%"></div></div>
				<span class="bar-count"><%= entry.getValue() %></span>
			</div>
			<% } %>
			<p class="hint">Passing grade: 3.00 (75%) and above. 4.00 is conditional.</p>
		</div>
	</section>

	<!-- ===== Per-student results ===== -->
	<div class="card">
		<div class="card-head">
			<h2>Student Results</h2>
			<input type="text" id="search" class="search no-print" placeholder="Search name or ID...">
		</div>
		<div class="table-wrap">
			<table id="resultsTable">
				<thead>
					<tr>
						<th class="num">Rank</th>
						<th>Student ID</th>
						<th>Name</th>
						<th class="num">Quiz</th>
						<th class="num">Assign.</th>
						<th class="num">Midterm</th>
						<th class="num">Final</th>
						<th class="num">Average</th>
						<th>Grade</th>
						<th>Remark</th>
						<th>Status</th>
						<th>Honors</th>
						<th>Remediation</th>
					</tr>
				</thead>
				<tbody>
					<% for (Student s : students) { %>
					<tr>
						<td class="num"><%= s.getRank() %></td>
						<td class="nowrap"><%= HtmlUtil.esc(s.getStudentId()) %></td>
						<td class="nowrap"><strong><%= HtmlUtil.esc(s.getName()) %></strong></td>
						<td class="num"><%= HtmlUtil.score(s.getQuiz()) %></td>
						<td class="num"><%= HtmlUtil.score(s.getAssignment()) %></td>
						<td class="num"><%= HtmlUtil.score(s.getMidterm()) %></td>
						<td class="num"><%= HtmlUtil.score(s.getFinalExam()) %></td>
						<td class="num"><strong><%= HtmlUtil.fmt(s.getAverage()) %></strong></td>
						<td class="nowrap"><strong><%= s.getNumericGrade() %></strong>
						<span class="badge <%= HtmlUtil.gradeClass(s.getLetterGrade()) %>"><%= s.getLetterGrade() %></span></td>
						<td class="remark"><%= s.getRemark() %></td>
						<td><span class="badge <%= HtmlUtil.statusClass(s.getStatus()) %>"><%= s.getStatus() %></span></td>
						<td class="col-honors <%= s.isHonorsAwarded() ? "honor" : "muted" %>"><%= s.getHonors() %></td>
						<td class="col-remediation <%= s.isNeedsRemediation() ? "flag" : "muted" %>"><%= s.getRemediation() %></td>
					</tr>
					<% } %>
				</tbody>
			</table>
		</div>
	</div>

	<!-- ===== Honor roll and remediation lists ===== -->
	<section class="grid-2">
		<div class="card" style="margin-bottom:0">
			<h2>Honor Roll</h2>
			<% if (report.getHonorRoll().isEmpty()) { %>
			<p class="empty">No students qualified for honors.</p>
			<% } else { %>
			<ul class="list">
				<% for (Student s : report.getHonorRoll()) { %>
				<li>
					<span>
						<span class="who"><%= HtmlUtil.esc(s.getName()) %></span><br>
						<span class="detail"><%= HtmlUtil.esc(s.getStudentId()) %> &middot; Rank <%= s.getRank() %></span>
					</span>
					<span style="text-align:right">
						<span class="honor"><%= s.getHonors() %></span><br>
						<span class="detail"><%= HtmlUtil.fmt(s.getAverage()) %> (<%= s.getNumericGrade() %> / <%= s.getLetterGrade() %>)</span>
					</span>
				</li>
				<% } %>
			</ul>
			<% } %>
		</div>

		<div class="card" style="margin-bottom:0">
			<h2>Remediation Required</h2>
			<% if (report.getRemediationList().isEmpty()) { %>
			<p class="empty">No students need remediation.</p>
			<% } else { %>
			<ul class="list">
				<% for (Student s : report.getRemediationList()) { %>
				<li>
					<span>
						<span class="who"><%= HtmlUtil.esc(s.getName()) %></span><br>
						<span class="detail"><%= HtmlUtil.esc(s.getStudentId()) %> &middot;
							<span class="badge <%= HtmlUtil.statusClass(s.getStatus()) %>"><%= s.getStatus() %></span></span>
					</span>
					<span style="text-align:right">
						<span class="flag"><%= s.getRemediation() %></span><br>
						<span class="detail"><%= HtmlUtil.fmt(s.getAverage()) %> (<%= s.getNumericGrade() %> / <%= s.getLetterGrade() %>)</span>
					</span>
				</li>
				<% } %>
			</ul>
			<% } %>
		</div>
	</section>
</main>

<%@ include file="/WEB-INF/includes/footer.jspf" %>

<script>
	// simple filter for the results table
	document.getElementById("search").addEventListener("input", function () {
		var term = this.value.toLowerCase();
		var rows = document.querySelectorAll("#resultsTable tbody tr");
		for (var i = 0; i < rows.length; i++) {
			var text = rows[i].cells[1].textContent + " " + rows[i].cells[2].textContent;
			rows[i].style.display = text.toLowerCase().indexOf(term) >= 0 ? "" : "none";
		}
	});
</script>
</body>
</html>
