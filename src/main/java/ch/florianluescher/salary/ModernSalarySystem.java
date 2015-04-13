package ch.florianluescher.salary;

import ch.florianluescher.salary.bank.Bank;
import ch.florianluescher.salary.hrsystem.EmployeeRecord;
import ch.florianluescher.salary.hrsystem.HRSystem;
import ch.florianluescher.salary.hrsystem.SalaryType;
import ch.florianluescher.salary.timetracker.TimeTracker;
import ch.florianluescher.salary.timetracker.TimeTrackingInformation;

public class ModernSalarySystem implements SalarySystem {

    private final Bank bank;
    private final HRSystem hrSystem;
    private final TimeTracker timeTracker;

    public ModernSalarySystem(Bank bank, HRSystem hrSystem, TimeTracker timeTracker) {
        this.bank = bank;
        this.hrSystem = hrSystem;
        this.timeTracker = timeTracker;
    }

    @Override
    public Salary paySalary(int employeeId) {
        try {
            return queryHrSystem(employeeId)
                    .filter(employeeRecord -> employeeRecord.isActive())
                    .flatMap(employeeInfo -> calculateSalary(employeeId, employeeInfo))
                    .ifPresent(salaryToPay -> bank.doTransaction(salaryToPay.getTransferredToIBAN(), salaryToPay.getAmount()))
                    .getOrElse(null);
        } catch (Exception ex) {
            return null;
        }
    }

    private Nullable<EmployeeRecord> queryHrSystem(int employeeId) {
        return Nullable.of(hrSystem.getEmployeeInfo(employeeId));
    }

    private Nullable<TimeTrackingInformation> queryTimeTrackingSystem(int employeeId) {
        return Nullable.of(timeTracker.getTimeTrackingInformation(employeeId));
    }

    private Nullable<Salary> calculateSalary(int employeeId, EmployeeRecord employeeInfo) {
        if (employeeInfo.getSalaryType() == SalaryType.MONTHLY) {
            return Nullable.of(new Salary(employeeInfo.getTargetIBAN(), employeeInfo.getSalary()));
        } else {
            return queryTimeTrackingSystem(employeeId)
                    .map(timeTrackingInformation -> timeTrackingInformation.getTotalHours() * employeeInfo.getSalary())
                    .map(amount -> new Salary(employeeInfo.getTargetIBAN(), amount));
        }
    }

    @Override
    public String toString() {
        return "ModernSalarySystem";
    }
}
