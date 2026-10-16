package com.grading.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.grading.model.ClassReport;
import com.grading.model.Student;
import com.grading.service.GradeEvaluator;
import com.grading.service.ReportGenerator;
import com.grading.service.RosterParser;
import com.grading.service.StatusRouter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/**
 * Controller for the grading pipeline.
 * Receives the roster from index.jsp, runs the four stages and sends the
 * user to results.jsp.
 */
@WebServlet("/process")
@MultipartConfig(maxFileSize = 1024 * 1024, maxRequestSize = 5 * 1024 * 1024)
public class ProcessServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// the pipeline only runs on form submission
		response.sendRedirect(request.getContextPath() + "/index.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		List<String> errors = new ArrayList<>();

		// An uploaded CSV file takes priority over the text box
		String uploadedText = null;
		String contentType = request.getContentType();
		if (contentType != null && contentType.startsWith("multipart/")) {
			try {
				Part filePart = request.getPart("rosterFile");
				if (filePart != null && filePart.getSize() > 0) {
					try (InputStream in = filePart.getInputStream()) {
						uploadedText = new String(in.readAllBytes(), StandardCharsets.UTF_8);
					}
				}
			} catch (IllegalStateException e) {
				errors.add("The uploaded file is too large. The limit is 1 MB.");
				showForm(request, response, errors, null, null);
				return;
			}
		}

		String course = request.getParameter("course");
		String rosterText = (uploadedText != null) ? uploadedText : request.getParameter("roster");

		// ---- Stage 1: iterative batch processing ----
		List<Student> students = RosterParser.parse(rosterText, errors);

		if (students.isEmpty()) {
			errors.add(0, "No valid student records were found. Check the format and try again.");
			showForm(request, response, errors, course, rosterText);
			return;
		}

		// ---- Stages 2 and 3: evaluate and route every student ----
		for (Student s : students) {
			GradeEvaluator.evaluate(s);
			StatusRouter.route(s);
		}

		// ---- Stage 4: report generation ----
		ClassReport report = ReportGenerator.generate(students, course);

		// keep the results in the session so results.jsp and the CSV export can use them
		HttpSession session = request.getSession();
		session.setAttribute("students", students);
		session.setAttribute("report", report);
		session.setAttribute("skippedLines", errors);

		response.sendRedirect(request.getContextPath() + "/results.jsp");
	}

	/** Sends the user back to the input form with error messages and their input. */
	private void showForm(HttpServletRequest request, HttpServletResponse response, List<String> errors,
			String course, String rosterText) throws ServletException, IOException {
		request.setAttribute("errors", errors);
		request.setAttribute("course", course);
		request.setAttribute("roster", rosterText);
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}
}
