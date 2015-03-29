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
        Salary salary = null;

        final Nullable<EmployeeRecord> employeeRecord = Nullable.fromReference(hrSystem.getEmployeeInfo(employeeId));
        if (employeeRecord.isPresent()) {
            final EmployeeRecord employeeInfo = employeeRecord.get();

            if (!employeeInfo.isActive()) {
                return salary;
            }

            if (employeeInfo.getSalaryType() == SalaryType.MONTHLY) {
                bank.doTransaction(employeeInfo.getTargetIBAN(), employeeInfo.getSalary());
                salary = new Salary(employeeInfo.getSalary());
            } else {
                final Nullable<TimeTrackingInformation> info = Nullable.fromReference(timeTracker.getTimeTrackingInformation(employeeId));
                if (info.isPresent()) {

                    final TimeTrackingInformation timeTrackingInformation = info.get();
                    final int hours = timeTrackingInformation.getTotalHours();

                    final int amount = hours * employeeInfo.getSalary();
                    bank.doTransaction(employeeInfo.getTargetIBAN(), amount);
                    salary = new Salary(amount);
                }
            }
        }

        return salary;
    }

    @Override
    public String toString() {
        return "ModernSalarySystem";
    }
}
