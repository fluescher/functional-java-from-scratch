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

        final Nullable<EmployeeRecord> optionalEmployeeInfo = Nullable.of(hrSystem.getEmployeeInfo(employeeId));
        return optionalEmployeeInfo
                    .filter(employeeRecord -> employeeRecord.isActive())
                    .flatMap(employeeInfo -> {
                        if (employeeInfo.getSalaryType() == SalaryType.MONTHLY) {
                            bank.doTransaction(employeeInfo.getTargetIBAN(), employeeInfo.getSalary());
                            return Nullable.of(new Salary(employeeInfo.getTargetIBAN(), employeeInfo.getSalary()));
                        } else {
                            final Nullable<TimeTrackingInformation> optionalTimeTrackingInformation = Nullable.of(timeTracker.getTimeTrackingInformation(employeeId));
                            return optionalTimeTrackingInformation.map(trackingInformation -> {
                                final int salary = employeeInfo.getSalary() * trackingInformation.getTotalHours();

                                bank.doTransaction(employeeInfo.getTargetIBAN(), salary);
                                return new Salary(employeeInfo.getTargetIBAN(), salary);
                            });
                        }
        }).getOrElse(null);
    }

    @Override
    public String toString() {
        return "ModernSalarySystem";
    }
}
