package com.grading.model;

import java.io.Serializable;

/**
 * One student record: the raw scores read from the roster plus every
 * value the grading pipeline computes for that student.
 */
public class Student implements Serializable {

	private static final long serialVersionUID = 2L;

	// ---- Raw input (filled by RosterParser) ----
	private String studentId;
	private String name;
	private double quiz;
	private double assignment;
	private double midterm;
	private double finalExam;

	// ---- Computed by GradeEvaluator ----
	private double average;
	private String letterGrade;
	private String numericGrade;

	// ---- Assigned by StatusRouter ----
	private String remark;
	private String status;
	private String honors;
	private boolean honorsAwarded;
	private String remediation;
	private boolean needsRemediation;

	// ---- Assigned by ReportGenerator ----
	private int rank;

	public Student(String studentId, String name, double quiz, double assignment, double midterm, double finalExam) {
		this.studentId = studentId;
		this.name = name;
		this.quiz = quiz;
		this.assignment = assignment;
		this.midterm = midterm;
		this.finalExam = finalExam;
	}

	/** Passing is 75 (3.00 / C-) or higher. D (4.00) is conditional and F (5.00) is failed. */
	public boolean isPassed() {
		return letterGrade != null && !letterGrade.equals("D") && !letterGrade.equals("F");
	}

	/** The lowest of the four component scores. */
	public double getLowestScore() {
		return Math.min(Math.min(quiz, assignment), Math.min(midterm, finalExam));
	}

	public String getStudentId() { return studentId; }
	public String getName() { return name; }
	public double getQuiz() { return quiz; }
	public double getAssignment() { return assignment; }
	public double getMidterm() { return midterm; }
	public double getFinalExam() { return finalExam; }

	public double getAverage() { return average; }
	public void setAverage(double average) { this.average = average; }

	public String getLetterGrade() { return letterGrade; }
	public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }

	public String getNumericGrade() { return numericGrade; }
	public void setNumericGrade(String numericGrade) { this.numericGrade = numericGrade; }

	public String getRemark() { return remark; }
	public void setRemark(String remark) { this.remark = remark; }

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }

	public String getHonors() { return honors; }
	public void setHonors(String honors) { this.honors = honors; }

	public boolean isHonorsAwarded() { return honorsAwarded; }
	public void setHonorsAwarded(boolean honorsAwarded) { this.honorsAwarded = honorsAwarded; }

	public String getRemediation() { return remediation; }
	public void setRemediation(String remediation) { this.remediation = remediation; }

	public boolean isNeedsRemediation() { return needsRemediation; }
	public void setNeedsRemediation(boolean needsRemediation) { this.needsRemediation = needsRemediation; }

	public int getRank() { return rank; }
	public void setRank(int rank) { this.rank = rank; }
}
