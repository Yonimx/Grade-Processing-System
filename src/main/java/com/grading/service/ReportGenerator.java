package com.grading.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.grading.model.ClassReport;
import com.grading.model.Student;

/**
 * STAGE 4 - Report Generation.
 *
 * Loops over the evaluated students to compile class metrics, the grade
 * distribution, class ranks, the honor roll and the remediation list.
 */
public class ReportGenerator {

	public static ClassReport generate(List<Student> students, String courseName) {
		ClassReport report = new ClassReport();

		if (courseName == null || courseName.isBlank()) {
			courseName = "Unnamed Class";
		}
		report.setCourseName(courseName.trim());
		report.setGeneratedOn(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a")));

		// start every letter grade and status at zero so they always appear in order
		Map<String, Integer> distribution = report.getGradeDistribution();
		for (String letter : GradeEvaluator.LETTER_GRADES) {
			distribution.put(letter, 0);
		}
		Map<String, Integer> statusCounts = report.getStatusCounts();
		statusCounts.put("Good Standing", 0);
		statusCounts.put("Academic Warning", 0);
		statusCounts.put("Conditional", 0);
		statusCounts.put("Failed", 0);

		report.setTotalStudents(students.size());
		if (students.isEmpty()) {
			return report;
		}

		double sum = 0;
		int passed = 0;
		int failed = 0;
		Student highest = students.get(0);
		Student lowest = students.get(0);

		for (Student s : students) {
			sum += s.getAverage();

			if (s.getAverage() > highest.getAverage()) {
				highest = s;
			}
			if (s.getAverage() < lowest.getAverage()) {
				lowest = s;
			}
			if (s.isPassed()) {
				passed++;
			}
			if (s.getLetterGrade().equals("F")) {
				failed++;
			}

			distribution.put(s.getLetterGrade(), distribution.get(s.getLetterGrade()) + 1);
			statusCounts.put(s.getStatus(), statusCounts.getOrDefault(s.getStatus(), 0) + 1);

			if (s.isHonorsAwarded()) {
				report.getHonorRoll().add(s);
			}
			if (s.isNeedsRemediation()) {
				report.getRemediationList().add(s);
			}
		}

		int total = students.size();
		report.setClassAverage(round(sum / total));
		report.setHighestAverage(highest.getAverage());
		report.setHighestName(highest.getName());
		report.setLowestAverage(lowest.getAverage());
		report.setLowestName(lowest.getName());
		report.setPassedCount(passed);
		report.setFailedCount(failed);
		report.setPassRate(round(passed * 100.0 / total));

		// rank students by average (highest first); tied averages share a rank
		List<Student> ranked = new ArrayList<>(students);
		ranked.sort(Comparator.comparingDouble(Student::getAverage).reversed());
		for (int i = 0; i < ranked.size(); i++) {
			Student s = ranked.get(i);
			if (i > 0 && s.getAverage() == ranked.get(i - 1).getAverage()) {
				s.setRank(ranked.get(i - 1).getRank());
			} else {
				s.setRank(i + 1);
			}
		}

		// median of the averages
		List<Double> averages = new ArrayList<>();
		for (Student s : students) {
			averages.add(s.getAverage());
		}
		Collections.sort(averages);
		int middle = total / 2;
		if (total % 2 == 1) {
			report.setMedian(averages.get(middle));
		} else {
			report.setMedian(round((averages.get(middle - 1) + averages.get(middle)) / 2));
		}

		// honor roll from best to lowest
		report.getHonorRoll().sort(Comparator.comparingDouble(Student::getAverage).reversed());
		// remediation list with the most urgent (lowest average) first
		report.getRemediationList().sort(Comparator.comparingDouble(Student::getAverage));

		return report;
	}

	private static double round(double value) {
		return Math.round(value * 100.0) / 100.0;
	}
}
