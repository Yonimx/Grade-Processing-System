<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	import="java.io.InputStream, java.nio.charset.StandardCharsets, java.util.ArrayList, java.util.List,
		com.grading.model.Student, com.grading.service.RosterParser, com.grading.util.HtmlUtil"%>
<%
	// Read the sample file with the same parser the app uses, so the preview matches what it accepts
	String sampleText = "";
	try (InputStream in = application.getResourceAsStream("/sample-roster.csv")) {
		if (in != null) {
			sampleText = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
	List<Student> rows = RosterParser.parse(sampleText, new ArrayList<String>());
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>GradeFlow - Sample CSV</title>
	<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/includes/nav.jspf" %>

<main class="container">

	<section class="report-head">
		<div>
			<h1>Sample CSV</h1>
			<span class="muted">This is the file format GradeFlow reads. Make your class list in Excel like this, then save it as CSV.</span>
		</div>
		<div class="btn-group">
			<a class="btn" href="<%= request.getContextPath() %>/sample-roster.csv" download>Download Sample CSV</a>
			<a class="btn btn-primary" href="<%= request.getContextPath() %>/index.jsp?sample=1">Try It With This Sample</a>
		</div>
	</section>

	<!-- ===== Column guide ===== -->
	<div class="card">
		<h2>Columns</h2>
		<div class="table-wrap">
			<table>
				<thead>
					<tr><th>Column</th><th>What to put</th><th>Example</th></tr>
				</thead>
				<tbody>
					<tr><td><strong>Student ID</strong></td><td>Any ID, must be unique</td><td>2024-0001</td></tr>
					<tr><td><strong>Name</strong></td><td>Full name. If it has a comma, put it in quotes</td><td>"Dela Cruz, Juan"</td></tr>
					<tr><td><strong>Quiz</strong></td><td>Score from 0 to 100</td><td>88</td></tr>
					<tr><td><strong>Assignment</strong></td><td>Score from 0 to 100</td><td>92</td></tr>
					<tr><td><strong>Midterm</strong></td><td>Score from 0 to 100</td><td>85</td></tr>
					<tr><td><strong>Final Exam</strong></td><td>Score from 0 to 100</td><td>90</td></tr>
				</tbody>
			</table>
		</div>
		<p class="hint">The first line (column names) is optional. Lines with missing or invalid values are skipped and listed on the results page.</p>
	</div>

	<!-- ===== Preview of the sample file ===== -->
	<div class="card">
		<div class="card-head">
			<h2>sample-roster.csv</h2>
			<span class="muted"><%= rows.size() %> students</span>
		</div>
		<div class="table-wrap">
			<table>
				<thead>
					<tr>
						<th>Student ID</th>
						<th>Name</th>
						<th class="num">Quiz</th>
						<th class="num">Assignment</th>
						<th class="num">Midterm</th>
						<th class="num">Final Exam</th>
					</tr>
				</thead>
				<tbody>
					<% for (Student s : rows) { %>
					<tr>
						<td class="nowrap"><%= HtmlUtil.esc(s.getStudentId()) %></td>
						<td class="nowrap"><%= HtmlUtil.esc(s.getName()) %></td>
						<td class="num"><%= HtmlUtil.score(s.getQuiz()) %></td>
						<td class="num"><%= HtmlUtil.score(s.getAssignment()) %></td>
						<td class="num"><%= HtmlUtil.score(s.getMidterm()) %></td>
						<td class="num"><%= HtmlUtil.score(s.getFinalExam()) %></td>
					</tr>
					<% } %>
				</tbody>
			</table>
		</div>
	</div>

	<!-- ===== Raw file text ===== -->
	<div class="card">
		<h2>Same file as plain text</h2>
		<p class="hint" style="margin-top:0">You can also paste lines like these straight into the text box on the Process Grades page.</p>
		<pre class="csv-text"><%= HtmlUtil.esc(sampleText.trim()) %></pre>
	</div>
</main>

<%@ include file="/WEB-INF/includes/footer.jspf" %>
</body>
</html>
