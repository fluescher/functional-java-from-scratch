package ch.florianluescher.salary.hrsystem;

public class EmployeeRecord {
    private final int employeeId;
    private final String lastname;
    private final String firstname;
    private final int salary;
    private final SalaryType salaryType;
    private final String targetIBAN;
    private final boolean active;

    public EmployeeRecord(int employeeId, String lastname, String firstname, int salary,
                          SalaryType salaryType, String targetIBAN, boolean active) {
        this.employeeId = employeeId;
        this.lastname = lastname;
        this.firstname = firstname;
        this.salary = salary;
        this.salaryType = salaryType;
        this.targetIBAN = targetIBAN;
        this.active = active;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public boolean isActive() {
        return active;
    }

    public int getSalary() {
        return salary;
    }

    public SalaryType getSalaryType() {
        return salaryType;
    }

    public String getTargetIBAN() {
        return targetIBAN;
    }
}
