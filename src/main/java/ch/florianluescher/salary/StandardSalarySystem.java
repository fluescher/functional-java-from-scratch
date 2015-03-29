package ch.florianluescher.salary;

import ch.florianluescher.salary.bank.Bank;
import ch.florianluescher.salary.hrsystem.EmployeeRecord;
import ch.florianluescher.salary.hrsystem.HRSystem;
import ch.florianluescher.salary.hrsystem.SalaryType;
import ch.florianluescher.salary.timetracker.TimeTracker;
import ch.florianluescher.salary.timetracker.TimeTrackingInformation;

public class StandardSalarySystem implements SalarySystem {

    private final Bank bank;
    private final HRSystem hrSystem;
    private final TimeTracker timeTracker;

    public StandardSalarySystem(Bank bank, HRSystem hrSystem, TimeTracker timeTracker) {
        this.bank = bank;
        this.hrSystem = hrSystem;
        this.timeTracker = timeTracker;
    }

    @Override
    public Salary paySalary(int employeeId) {

        final EmployeeRecord employeeInfo = hrSystem.getEmployeeInfo(employeeId);
        if (employeeInfo == null) return null;
        if(!employeeInfo.isActive()) return null;

        if (employeeInfo.getSalaryType() == SalaryType.MONTHLY) {
            bank.doTransaction(employeeInfo.getTargetIBAN(), employeeInfo.getSalary());
            return new Salary(employeeInfo.getSalary());
        } else {
            final TimeTrackingInformation timeTrackingInformation = timeTracker.getTimeTrackingInformation(employeeId);
            if(timeTrackingInformation == null) return null;

            final int hours = timeTrackingInformation.getTotalHours();

            final int amount = hours * employeeInfo.getSalary();
            bank.doTransaction(employeeInfo.getTargetIBAN(), amount);
            return new Salary(amount);
        }
    }

    @Override
    public String toString() {
        return "StandardSalarySystem";
    }
}
