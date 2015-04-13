package ch.florianluescher.salary;

import ch.florianluescher.salary.bank.Bank;
import ch.florianluescher.salary.hrsystem.EmployeeRecord;
import ch.florianluescher.salary.hrsystem.HRSystem;
import ch.florianluescher.salary.timetracker.TimeTracker;
import ch.florianluescher.salary.timetracker.TimeTrackingInformation;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static ch.florianluescher.salary.hrsystem.SalaryType.HOURLY;
import static ch.florianluescher.salary.hrsystem.SalaryType.MONTHLY;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class SalarySystemTest {

    private final SalarySystem salarySystem;

    private static final Bank bankMock = mock(Bank.class);
    private static final HRSystem hrSystemMock = mock(HRSystem.class);
    private static final TimeTracker timeTrackerMock = mock(TimeTracker.class);

    public SalarySystemTest(SalarySystem salarySystem) {
        this.salarySystem = salarySystem;
    }

    @Test
    public void paySalary_whenEmployeeHasFixedMonthlyPayCheck_doesPaySalary() {

        final EmployeeRecord monthlyPayedEmployee = new EmployeeRecord(1297, "Muster", "Max", 4850, MONTHLY, "IBAN", true);
        when(hrSystemMock.getEmployeeInfo(1297)).thenReturn(monthlyPayedEmployee);

        Salary paidSalary = salarySystem.paySalary(1297);

        assertEquals(4850, paidSalary.getAmount());
        verify(bankMock).doTransaction("IBAN", 4850);
    }

    @Test
    public void paySalary_whenEmployeeHasHourlySalary_doesPayTheSalary() {

        final EmployeeRecord hourlyPayedEmployee = new EmployeeRecord(1297, "Muster", "Max", 84, HOURLY, "IBAN", true);
        when(hrSystemMock.getEmployeeInfo(1297)).thenReturn(hourlyPayedEmployee);
        when(timeTrackerMock.getTimeTrackingInformation(1297)).thenReturn(new TimeTrackingInformation(190, 5));

        Salary paidSalary = salarySystem.paySalary(1297);

        assertEquals(84 * 195, paidSalary.getAmount());
        verify(bankMock).doTransaction("IBAN", 84 * 195);
    }

    @Test
    public void paySalary_whenEmployeeHasHourlySalaryButNoTimeInformation_doesNotPayTheSalary() {

        final EmployeeRecord hourlyPayedEmployee = new EmployeeRecord(1297, "Muster", "Max", 84, HOURLY, "IBAN", true);
        when(hrSystemMock.getEmployeeInfo(1297)).thenReturn(hourlyPayedEmployee);
        when(timeTrackerMock.getTimeTrackingInformation(1297)).thenReturn(null);

        Salary paidSalary = salarySystem.paySalary(1297);

        assertNull(paidSalary);
        verify(bankMock, never()).doTransaction(anyString(), anyInt());
    }

    @Test
    public void paySalary_whenEmployeeHasLeftTheCompany_doesNotPayASalary() {

        final EmployeeRecord formerEmployee = new EmployeeRecord(1297, "Meier", "Hans", 5540, MONTHLY, "IBAN", false);
        when(hrSystemMock.getEmployeeInfo(1297)).thenReturn(formerEmployee);

        Salary paidSalary = salarySystem.paySalary(1297);

        assertEquals(null, paidSalary);
        verify(bankMock, never()).doTransaction(anyString(), anyInt());
    }

    @Test
    public void paySalary_whenTheEmployeeDoesNotExists_doesNotPayASalary() {

        when(hrSystemMock.getEmployeeInfo(1297)).thenReturn(null);

        Salary paidSalary = salarySystem.paySalary(1297);

        assertNull(paidSalary);
        verify(bankMock, never()).doTransaction(anyString(), anyInt());
    }

    @After
    public void resetMocks() {
        reset(bankMock);
        reset(hrSystemMock);
        reset(timeTrackerMock);
    }

    @Parameterized.Parameters(name="{0}")
    public static Collection<SalarySystem> getInstancesToTest() {
        final StandardSalarySystem standardSalarySystem = new StandardSalarySystem(bankMock, hrSystemMock, timeTrackerMock);
        final ModernSalarySystem modernSalarySystem = new ModernSalarySystem(bankMock, hrSystemMock, timeTrackerMock);

        return asList(standardSalarySystem, modernSalarySystem);
    }
}
