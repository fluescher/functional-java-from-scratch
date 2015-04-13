package ch.florianluescher.salary;

import ch.florianluescher.salary.bank.Bank;
import ch.florianluescher.salary.hrsystem.EmployeeRecord;
import ch.florianluescher.salary.hrsystem.HRSystem;
import ch.florianluescher.salary.hrsystem.SalaryType;
import ch.florianluescher.salary.timetracker.TimeTracker;
import ch.florianluescher.salary.timetracker.TimeTrackingInformation;

import java.util.Optional;

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

        final Nullable<EmployeeRecord> employeeInfo = Nullable.of(hrSystem.getEmployeeInfo(employeeId));
        if(!employeeInfo.isPresent()) return null;
        if(!employeeInfo.get().isActive()) return null;

        if(employeeInfo.get().getSalaryType() == SalaryType.MONTHLY) {
            bank.doTransaction(employeeInfo.get().getTargetIBAN(), employeeInfo.get().getSalary());
            return new Salary(employeeInfo.get().getTargetIBAN(), employeeInfo.get().getSalary());
        } else {
            final Nullable<TimeTrackingInformation> timeTrackingInformation = Nullable.of(timeTracker.getTimeTrackingInformation(employeeId));
            if(!timeTrackingInformation.isPresent()) return null;

            final int salary = employeeInfo.get().getSalary() * timeTrackingInformation.get().getTotalHours();

            bank.doTransaction(employeeInfo.get().getTargetIBAN(), salary);
            return new Salary(employeeInfo.get().getTargetIBAN(), salary);
        }
    }

    @Override
    public String toString() {
        return "ModernSalarySystem";
    }
}
