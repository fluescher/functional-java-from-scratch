package ch.florianluescher.salary.hrsystem;

public interface HRSystem {
    /**
     * Queries the HR System for the information of an employee.
     *
     * @param employeeId the ID of the employee.
     * @return the information regarding the employee identified with given Employee Id
     * or <code>null</code> if no such employee was found
     */
    EmployeeRecord getEmployeeInfo(int employeeId);
}
