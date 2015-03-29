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
        final Nullable<EmployeeRecord> employeeRecord = Nullable.fromReference(hrSystem.getEmployeeInfo(employeeId));
        Salary salary = null;

        if (employeeRecord.isPresent()) {

            if (employeeRecord.get().isActive()) {

                if (employeeRecord.get().getSalaryType() == SalaryType.MONTHLY) {
                    bank.doTransaction(employeeRecord.get().getTargetIBAN(), employeeRecord.get().getSalary());
                    salary = new Salary(employeeRecord.get().getSalary());
                } else {
                    final Nullable<TimeTrackingInformation> info = Nullable.fromReference(timeTracker.getTimeTrackingInformation(employeeId));
                    if (info.isPresent()) {

                        final int hours = info.get().getTotalHours();

                        final int amount = hours * employeeRecord.get().getSalary();
                        bank.doTransaction(employeeRecord.get().getTargetIBAN(), amount);
                        salary = new Salary(amount);
                    }
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
