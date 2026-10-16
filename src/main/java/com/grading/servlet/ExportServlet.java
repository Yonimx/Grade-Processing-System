package com.grading.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import com.grading.model.ClassReport;
import com.grading.model.Student;
import com.grading.service.GradeEvaluator;
import com.grading.util.HtmlUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Downloads the last processed class as a CSV report that opens in Excel.
 */
@WebServlet("/export")
public class ExportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		List<Student> students = (session == null) ? null : (List<Student>) session.getAttribute("students");
		ClassReport report = (session == null) ? null : (ClassReport) session.getAttribute("report");

		if (students == null || report == null) {
			response.sendRedirect(request.getContextPath() + "/index.jsp");
			return;
		}

		response.setContentType("text/csv; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=\"grade_report.csv\"");

		PrintWriter out = response.getWriter();
		out.print('﻿'); // lets Excel detect UTF-8 (names with accents, n with tilde, etc.)

		out.println("Grade Report," + csv(report.getCourseName()));
		out.println("Generated," + csv(report.getGeneratedOn()));
		out.println();

		out.println("Rank,Student ID,Name,Quiz,Assignment,Midterm,Final Exam,Weighted Average,Grade,Letter,"
				+ "Remark,Status,Honors,Remediation");
		for (Student s : students) {
			out.println(s.getRank() + ","
					+ csv(s.getStudentId()) + ","
					+ csv(s.getName()) + ","
					+ HtmlUtil.fmt(s.getQuiz()) + ","
					+ HtmlUtil.fmt(s.getAssignment()) + ","
					+ HtmlUtil.fmt(s.getMidterm()) + ","
					+ HtmlUtil.fmt(s.getFinalExam()) + ","
					+ HtmlUtil.fmt(s.getAverage()) + ","
					+ s.getNumericGrade() + ","
					+ s.getLetterGrade() + ","
					+ csv(s.getRemark()) + ","
					+ csv(s.getStatus()) + ","
					+ csv(s.getHonors()) + ","
					+ csv(s.getRemediation()));
		}

		out.println();
		out.println("Class Summary");
		out.println("Total Students," + report.getTotalStudents());
		out.println("Class Average," + HtmlUtil.fmt(report.getClassAverage()));
		out.println("Median," + HtmlUtil.fmt(report.getMedian()));
		out.println("Highest," + HtmlUtil.fmt(report.getHighestAverage()) + "," + csv(report.getHighestName()));
		out.println("Lowest," + HtmlUtil.fmt(report.getLowestAverage()) + "," + csv(report.getLowestName()));
		out.println("Passed," + report.getPassedCount());
		out.println("Conditional," + report.getStatusCounts().get("Conditional"));
		out.println("Failed," + report.getFailedCount());
		out.println("Pass Rate (%)," + HtmlUtil.fmt(report.getPassRate()));
		out.println("Honor Roll," + report.getHonorRoll().size());
		out.println("Flagged for Remediation," + report.getRemediationList().size());

		out.println();
		out.println("Grade Distribution");
		out.println("Grade,Letter,Students");
		for (Map.Entry<String, Integer> entry : report.getGradeDistribution().entrySet()) {
			out.println(GradeEvaluator.toNumericGrade(entry.getKey()) + "," + entry.getKey() + "," + entry.getValue());
		}

		out.println();
		out.println("Academic Status");
		for (Map.Entry<String, Integer> entry : report.getStatusCounts().entrySet()) {
			out.println(entry.getKey() + "," + entry.getValue());
		}
	}

	/** Wraps a value in quotes so commas inside it do not split the column. */
	private static String csv(String value) {
		if (value == null) {
			return "";
		}
		return "\"" + value.replace("\"", "\"\"") + "\"";
	}
}
