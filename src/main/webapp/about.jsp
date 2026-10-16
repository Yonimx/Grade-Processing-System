<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	import="com.grading.util.HtmlUtil, com.grading.service.GradeEvaluator, com.grading.service.StatusRouter"%>
<%
	String[] letters = GradeEvaluator.LETTER_GRADES;
	String[] ranges = { "97 - 100", "93 - 96.99", "90 - 92.99", "87 - 89.99", "83 - 86.99", "80 - 82.99",
			"77 - 79.99", "73 - 76.99", "70 - 72.99", "60 - 69.99", "below 60" };
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>GradeFlow - How It Works</title>
	<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/includes/nav.jspf" %>

<main class="container">

	<section class="hero">
		<h1>How It Works</h1>
		<p>GradeFlow is a data pipeline: a roster of raw scores goes in, passes through four processing
			stages, and comes out as a structured class performance report.</p>
	</section>

	<!-- ===== The four stages ===== -->
	<section class="stage-grid">
		<div class="stage">
			<div class="num">1</div>
			<h3>Iterative Batch Processing</h3>
			<p>A <code>for</code> loop reads the roster line by line, checks each record (6 values, numeric
				scores from 0 to 100, unique student ID) and skips invalid lines with a clear message.</p>
			<span class="code">RosterParser.java</span>
		</div>
		<div class="stage">
			<div class="num">2</div>
			<h3>Conditional Evaluation</h3>
			<p>Computes the weighted average, then an <code>if / else if</code> ladder of eleven
				thresholds converts it into a letter grade from A+ to F.</p>
			<span class="code">GradeEvaluator.java</span>
		</div>
		<div class="stage">
			<div class="num">3</div>
			<h3>State Routing</h3>
			<p><code>switch</code> statements on the letter grade attach a tailored remark, the academic
				status, the honors classification and the remediation flag.</p>
			<span class="code">StatusRouter.java</span>
		</div>
		<div class="stage">
			<div class="num">4</div>
			<h3>Report Generation</h3>
			<p>Loops over the whole class to compute the average, median, pass rate, ranks, grade
				distribution, honor roll and remediation list. Exportable as CSV.</p>
			<span class="code">ReportGenerator.java</span>
		</div>
	</section>

	<!-- ===== Grading policy (built from the same switch logic the system uses) ===== -->
	<div class="card">
		<h2>Grading Policy</h2>
		<p class="muted" style="margin-top:0">
			Weighted average = Quiz &times; <%= Math.round(GradeEvaluator.QUIZ_WEIGHT * 100) %>%
			+ Assignment &times; <%= Math.round(GradeEvaluator.ASSIGNMENT_WEIGHT * 100) %>%
			+ Midterm &times; <%= Math.round(GradeEvaluator.MIDTERM_WEIGHT * 100) %>%
			+ Final Exam &times; <%= Math.round(GradeEvaluator.FINAL_WEIGHT * 100) %>%
		</p>
		<div class="table-wrap">
			<table>
				<thead>
					<tr>
						<th>Grade</th>
						<th>Average</th>
						<th>Remark</th>
						<th>Academic Status</th>
						<th>Honors</th>
						<th>Remediation</th>
					</tr>
				</thead>
				<tbody>
					<% for (int i = 0; i < letters.length; i++) { %>
					<tr>
						<td><span class="badge <%= HtmlUtil.gradeClass(letters[i]) %>"><%= letters[i] %></span></td>
						<td class="nowrap"><%= ranges[i] %></td>
						<td class="remark"><%= StatusRouter.remarkFor(letters[i]) %></td>
						<td><span class="badge <%= HtmlUtil.statusClass(StatusRouter.statusFor(letters[i])) %>"><%= StatusRouter.statusFor(letters[i]) %></span></td>
						<td class="nowrap"><%= StatusRouter.honorsFor(letters[i]) %></td>
						<td><%= StatusRouter.remediationFor(letters[i]) %></td>
					</tr>
					<% } %>
				</tbody>
			</table>
		</div>
		<h3 style="margin-top:18px">Additional rules</h3>
		<ul>
			<li>Honors are <strong>withheld</strong> if any single component score is below
				<%= (int) StatusRouter.HONORS_MIN_COMPONENT %>.</li>
			<li>A passing student with any component below <%= (int) StatusRouter.WEAK_COMPONENT %> is still
				flagged with <em>Review weak area</em> for that component.</li>
			<li>Students with the same average share the same class rank.</li>
		</ul>
	</div>

	<!-- ===== Architecture ===== -->
	<div class="card">
		<h2>Application Flow (MVC)</h2>
		<div class="flow">
			<span class="box">index.jsp<br><small class="muted">View: roster form</small></span><span>&rarr;</span>
			<span class="box">ProcessServlet<br><small class="muted">Controller: /process</small></span><span>&rarr;</span>
			<span class="box">RosterParser &rarr; GradeEvaluator &rarr; StatusRouter &rarr; ReportGenerator<br><small class="muted">Service: the pipeline</small></span><span>&rarr;</span>
			<span class="box">results.jsp<br><small class="muted">View: class report</small></span><span>&rarr;</span>
			<span class="box">ExportServlet<br><small class="muted">Controller: /export (CSV)</small></span>
		</div>
		<p class="hint">Model classes: <code>Student</code> (one record) and <code>ClassReport</code> (class summary).</p>
	</div>

	<!-- ===== Team (edit with your group's names) ===== -->
	<div class="card">
		<h2>Project Team</h2>
		<div class="table-wrap">
			<table>
				<thead>
					<tr><th>Member</th><th>Role</th><th>Responsible for</th></tr>
				</thead>
				<tbody>
					<tr><td>Member 1</td><td>Project Lead / Integration</td><td>ProcessServlet, ExportServlet, web.xml</td></tr>
					<tr><td>Member 2</td><td>Batch Processing</td><td>RosterParser, Student model</td></tr>
					<tr><td>Member 3</td><td>Grade Evaluation &amp; Routing</td><td>GradeEvaluator, StatusRouter</td></tr>
					<tr><td>Member 4</td><td>Reporting</td><td>ReportGenerator, ClassReport model</td></tr>
					<tr><td>Member 5</td><td>User Interface</td><td>JSP pages, style.css</td></tr>
				</tbody>
			</table>
		</div>
	</div>
</main>

<%@ include file="/WEB-INF/includes/footer.jspf" %>
</body>
</html>
