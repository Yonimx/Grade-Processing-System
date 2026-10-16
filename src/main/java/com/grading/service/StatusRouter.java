package com.grading.service;

import com.grading.model.Student;

/**
 * STAGE 3 - State Routing.
 *
 * Uses switch statements on the letter grade to attach a tailored remark,
 * an academic status, an honors classification and a remediation flag.
 */
public class StatusRouter {

	public static final String NONE = "None";

	// Honors are withheld if any single component is below this score
	public static final double HONORS_MIN_COMPONENT = 85;
	// Passing students with a component below this score are flagged for review
	public static final double WEAK_COMPONENT = 70;

	public static void route(Student s) {
		String letter = s.getLetterGrade();

		s.setRemark(remarkFor(letter));
		s.setStatus(statusFor(letter));

		// Honors: the grade decides the tier, but every component must also be strong
		String honors = honorsFor(letter);
		boolean awarded = !honors.equals(NONE);
		if (awarded && s.getLowestScore() < HONORS_MIN_COMPONENT) {
			honors = "Withheld (a component is below " + (int) HONORS_MIN_COMPONENT + ")";
			awarded = false;
		}
		s.setHonors(honors);
		s.setHonorsAwarded(awarded);

		// Remediation: the grade decides first; passing students are still
		// flagged when one component is weak
		String remediation = remediationFor(letter);
		if (remediation.equals(NONE) && s.getLowestScore() < WEAK_COMPONENT) {
			remediation = "Review weak area: " + weakestComponent(s);
		}
		s.setRemediation(remediation);
		s.setNeedsRemediation(!remediation.equals(NONE));
	}

	/** Tailored feedback for every letter grade. */
	public static String remarkFor(String letter) {
		String remark;
		switch (letter) {
			case "A+":
				remark = "Outstanding - exceptional mastery of the course";
				break;
			case "A":
				remark = "Excellent - strong command of all topics";
				break;
			case "A-":
				remark = "Very good - consistently high performance";
				break;
			case "B+":
				remark = "Good - solid understanding with minor gaps";
				break;
			case "B":
				remark = "Good - meets expectations well";
				break;
			case "B-":
				remark = "Above average - keep building consistency";
				break;
			case "C+":
				remark = "Satisfactory - fair grasp of the material";
				break;
			case "C":
				remark = "Satisfactory - meets minimum expectations";
				break;
			case "C-":
				remark = "Fair - needs to strengthen the fundamentals";
				break;
			case "D":
				remark = "Poor - significant gaps in understanding";
				break;
			case "F":
				remark = "Failing - course requirements not met";
				break;
			default:
				remark = "No remark available";
		}
		return remark;
	}

	/** Administrative status attached to the student's record. */
	public static String statusFor(String letter) {
		String status;
		switch (letter) {
			case "A+":
			case "A":
			case "A-":
			case "B+":
			case "B":
			case "B-":
			case "C+":
			case "C":
				status = "Good Standing";
				break;
			case "C-":
				status = "Academic Warning";
				break;
			case "D":
				status = "Probation";
				break;
			case "F":
				status = "Failed";
				break;
			default:
				status = "Unknown";
		}
		return status;
	}

	/** Honors classification based on the letter grade alone. */
	public static String honorsFor(String letter) {
		String honors;
		switch (letter) {
			case "A+":
				honors = "With Highest Honors";
				break;
			case "A":
				honors = "With High Honors";
				break;
			case "A-":
				honors = "With Honors";
				break;
			default:
				honors = NONE;
		}
		return honors;
	}

	/** Remediation requirement based on the letter grade alone. */
	public static String remediationFor(String letter) {
		String remediation;
		switch (letter) {
			case "C-":
				remediation = "Tutorial sessions recommended";
				break;
			case "D":
				remediation = "Remedial program required";
				break;
			case "F":
				remediation = "Must retake the course";
				break;
			default:
				remediation = NONE;
		}
		return remediation;
	}

	/** Name of the component with the lowest score. */
	private static String weakestComponent(Student s) {
		String[] names = { "Quiz", "Assignment", "Midterm", "Final Exam" };
		double[] scores = { s.getQuiz(), s.getAssignment(), s.getMidterm(), s.getFinalExam() };

		int lowest = 0;
		for (int i = 1; i < scores.length; i++) {
			if (scores[i] < scores[lowest]) {
				lowest = i;
			}
		}
		return names[lowest];
	}
}
