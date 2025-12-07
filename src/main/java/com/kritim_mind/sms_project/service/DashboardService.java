package com.kritim_mind.sms_project.service;

import com.kritim_mind.sms_project.dto.response.DailyReportData;
import com.kritim_mind.sms_project.dto.response.DashboardResponse;
import com.kritim_mind.sms_project.model.Admin;
import com.kritim_mind.sms_project.repository.AdminRepository;
import com.kritim_mind.sms_project.repository.ContactRepository;
import com.kritim_mind.sms_project.repository.GroupRepository;
import com.kritim_mind.sms_project.repository.MessageRepository;
import com.kritim_mind.sms_project.utils.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final AdminRepository adminRepository;
    private final MessageRepository messageRepository;
    private final ContactRepository contactRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public DashboardResponse getDashboardSummary(Long adminId) {
        log.info("Fetching dashboard summary for admin ID: {}", adminId);

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        LocalDateTime yesterday = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime today = LocalDate.now().atStartOfDay();

        Long totalSmsSentYesterday = messageRepository.sumSmsPartsBySenderAndDateRange(
                adminId, yesterday, today);

        Long totalTransactions = messageRepository.countBySenderAndDateRange(
                adminId, LocalDateTime.MIN, LocalDateTime.now());

        Long totalSmsLength = messageRepository.sumSmsPartsBySenderAndDateRange(
                adminId, LocalDateTime.MIN, LocalDateTime.now());

        long contactsCount = contactRepository.countActiveContacts();
        long groupsCount = groupRepository.countActiveGroups();

        return DashboardResponse.builder()
                .totalSmsSentYesterday(totalSmsSentYesterday != null ? totalSmsSentYesterday : 0L)
                .totalTransactions(totalTransactions != null ? totalTransactions : 0L)
                .totalSmsLength(totalSmsLength != null ? totalSmsLength : 0L)
                .remainingBalance(admin.getRemainingCredits())
                .contactsCount(contactsCount)
                .groupsCount(groupsCount)
                .build();
    }

    @Transactional
    public List<DailyReportData> getDailyReport(Long adminId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching daily report for admin ID: {} from {} to {}",
                adminId, startDate, endDate);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object[]> results = messageRepository.getDailySmsUsage(adminId, start, end);

        List<DailyReportData> reportData = new ArrayList<>();
        for (Object[] result : results) {
            Date date = (Date) result[0];
            Long total = ((Number) result[1]).longValue();
            reportData.add(new DailyReportData(date.toLocalDate(), total));
        }

        return reportData;
    }

    @Transactional
    public List<DailyReportData> getMonthlyReport(Long adminId, int year) {
        log.info("Fetching monthly report for admin ID: {} for year {}", adminId, year);

        List<DailyReportData> monthlyData = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

            Long total = messageRepository.sumSmsPartsBySenderAndDateRange(adminId, start, end);
            monthlyData.add(new DailyReportData(
                    LocalDate.of(year, month, 1),
                    total != null ? total : 0L
            ));
        }

        return monthlyData;
    }
}

