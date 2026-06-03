package yu.likelion14th.allligo_was.domains.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.dashboard.dto.response.DashboardStatisticsResDto;
import yu.likelion14th.allligo_was.domains.dashboard.repository.DashboardQueryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final DashboardQueryRepository dashboardQueryRepository;

    public DashboardStatisticsResDto getStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = resolveStartDate(startDate);
        LocalDateTime end = resolveEndDate(endDate);

        Long totalClicks = dashboardQueryRepository.countTotalClicks(userId, start, end);

        DashboardStatisticsResDto.HourlyClickStatisticsDto hourlyClickStatistics =
                buildHourlyClickStatistics(dashboardQueryRepository.findHourlyClicks(userId, start, end));

        DashboardStatisticsResDto.DayOfWeekClickStatisticsDto dayOfWeekClickStatistics =
                buildDayOfWeekClickStatistics(dashboardQueryRepository.findDayOfWeekClicks(userId, start, end));

        DashboardStatisticsResDto.TagClickStatisticsDto tagClickStatistics =
                buildTagClickStatistics(dashboardQueryRepository.findTagClicks(userId, start, end));

        DashboardStatisticsResDto.ContentTypeClickStatisticsDto contentTypeClickStatistics =
                buildContentTypeClickStatistics(dashboardQueryRepository.findContentTypeClicks(userId, start, end));

        return DashboardStatisticsResDto.builder()
                .totalClicks(totalClicks)
                .hourlyClickStatistics(hourlyClickStatistics)
                .dayOfWeekClickStatistics(dayOfWeekClickStatistics)
                .tagClickStatistics(tagClickStatistics)
                .contentTypeClickStatistics(contentTypeClickStatistics)
                .build();
    }

    private LocalDateTime resolveStartDate(LocalDate startDate) {
        if (startDate != null) {
            return startDate.atStartOfDay();
        }

        return YearMonth.now().atDay(1).atStartOfDay();
    }

    private LocalDateTime resolveEndDate(LocalDate endDate) {
        if (endDate != null) {
            return endDate.plusDays(1).atStartOfDay();
        }

        return YearMonth.now().plusMonths(1).atDay(1).atStartOfDay();
    }

    private DashboardStatisticsResDto.HourlyClickStatisticsDto buildHourlyClickStatistics(List<Object[]> rows) {
        Map<Integer, Long> countMap = rows.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(),
                        row -> ((Number) row[1]).longValue()
                ));

        List<DashboardStatisticsResDto.HourlyClickDto> hourlyClicks = new ArrayList<>();

        for (int hour = 0; hour <= 23; hour++) {
            hourlyClicks.add(DashboardStatisticsResDto.HourlyClickDto.builder()
                    .hour(hour)
                    .clickCount(countMap.getOrDefault(hour, 0L))
                    .build());
        }

        Long maxClickCount = hourlyClicks.stream()
                .mapToLong(DashboardStatisticsResDto.HourlyClickDto::getClickCount)
                .max()
                .orElse(0L);

        Long minClickCount = hourlyClicks.stream()
                .mapToLong(DashboardStatisticsResDto.HourlyClickDto::getClickCount)
                .min()
                .orElse(0L);

        Integer maxHour = hourlyClicks.stream()
                .filter(item -> item.getClickCount().equals(maxClickCount))
                .map(DashboardStatisticsResDto.HourlyClickDto::getHour)
                .findFirst()
                .orElse(0);

        Integer minHour = hourlyClicks.stream()
                .filter(item -> item.getClickCount().equals(minClickCount))
                .map(DashboardStatisticsResDto.HourlyClickDto::getHour)
                .findFirst()
                .orElse(0);

        Long averageClickCount = Math.round(
                hourlyClicks.stream()
                        .mapToLong(DashboardStatisticsResDto.HourlyClickDto::getClickCount)
                        .average()
                        .orElse(0.0)
        );

        return DashboardStatisticsResDto.HourlyClickStatisticsDto.builder()
                .averageClickCount(averageClickCount)
                .maxClickCount(maxClickCount)
                .maxHour(maxHour)
                .minClickCount(minClickCount)
                .minHour(minHour)
                .hourlyClicks(hourlyClicks)
                .build();
    }

    private DashboardStatisticsResDto.DayOfWeekClickStatisticsDto buildDayOfWeekClickStatistics(List<Object[]> rows) {
        Map<Integer, Long> countMap = rows.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(),
                        row -> ((Number) row[1]).longValue()
                ));

        List<DashboardStatisticsResDto.DayOfWeekClickDto> baseDays = List.of(
                buildDay("MON", "월", countMap.getOrDefault(2, 0L)),
                buildDay("TUE", "화", countMap.getOrDefault(3, 0L)),
                buildDay("WED", "수", countMap.getOrDefault(4, 0L)),
                buildDay("THU", "목", countMap.getOrDefault(5, 0L)),
                buildDay("FRI", "금", countMap.getOrDefault(6, 0L)),
                buildDay("SAT", "토", countMap.getOrDefault(7, 0L)),
                buildDay("SUN", "일", countMap.getOrDefault(1, 0L))
        );

        List<Long> distinctCounts = baseDays.stream()
                .map(DashboardStatisticsResDto.DayOfWeekClickDto::getClickCount)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        Map<Long, Integer> rankMap = new HashMap<>();

        for (int i = 0; i < distinctCounts.size(); i++) {
            rankMap.put(distinctCounts.get(i), i + 1);
        }

        List<DashboardStatisticsResDto.DayOfWeekClickDto> rankedDays = baseDays.stream()
                .map(day -> DashboardStatisticsResDto.DayOfWeekClickDto.builder()
                        .dayOfWeek(day.getDayOfWeek())
                        .label(day.getLabel())
                        .clickCount(day.getClickCount())
                        .rank(rankMap.get(day.getClickCount()))
                        .build())
                .toList();

        Long maxClickCount = distinctCounts.isEmpty() ? 0L : distinctCounts.get(0);
        Long secondClickCount = distinctCounts.size() >= 2 ? distinctCounts.get(1) : 0L;

        return DashboardStatisticsResDto.DayOfWeekClickStatisticsDto.builder()
                .maxClickCount(maxClickCount)
                .secondClickCount(secondClickCount)
                .dayOfWeekClicks(rankedDays)
                .build();
    }

    private DashboardStatisticsResDto.DayOfWeekClickDto buildDay(String dayOfWeek, String label, Long count) {
        return DashboardStatisticsResDto.DayOfWeekClickDto.builder()
                .dayOfWeek(dayOfWeek)
                .label(label)
                .clickCount(count)
                .rank(0)
                .build();
    }

    private DashboardStatisticsResDto.TagClickStatisticsDto buildTagClickStatistics(List<Object[]> rows) {
        if (rows.isEmpty()) {
            return DashboardStatisticsResDto.TagClickStatisticsDto.builder()
                    .topTagClickRatios(List.of())
                    .build();
        }

        List<Object[]> topRows = rows.stream()
                .limit(3)
                .toList();

        long total = topRows.stream()
                .mapToLong(row -> ((Number) row[1]).longValue())
                .sum();

        if (total == 0) {
            return DashboardStatisticsResDto.TagClickStatisticsDto.builder()
                    .topTagClickRatios(List.of())
                    .build();
        }

        List<DashboardStatisticsResDto.TagClickRatioDto> ratios = new ArrayList<>();
        int accumulatedRatio = 0;

        for (int i = 0; i < topRows.size(); i++) {
            Object[] row = topRows.get(i);
            String tagName = String.valueOf(row[0]);
            Long clickCount = ((Number) row[1]).longValue();

            int ratio;

            if (i == topRows.size() - 1) {
                ratio = 100 - accumulatedRatio;
            } else {
                ratio = (int) Math.round((clickCount * 100.0) / total);
                accumulatedRatio += ratio;
            }

            ratios.add(DashboardStatisticsResDto.TagClickRatioDto.builder()
                    .tagName(tagName)
                    .clickCount(clickCount)
                    .ratio(ratio)
                    .build());
        }

        return DashboardStatisticsResDto.TagClickStatisticsDto.builder()
                .topTagClickRatios(ratios)
                .build();
    }

    private DashboardStatisticsResDto.ContentTypeClickStatisticsDto buildContentTypeClickStatistics(List<Object[]> rows) {
        Map<String, Long> countMap = rows.stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]),
                        row -> ((Number) row[1]).longValue()
                ));

        Long blogCount = countMap.getOrDefault("BLOG", 0L);
        Long videoCount = countMap.getOrDefault("VIDEO", 0L);
        Long total = blogCount + videoCount;

        int blogRatio = total == 0 ? 0 : (int) Math.round((blogCount * 100.0) / total);
        int videoRatio = total == 0 ? 0 : 100 - blogRatio;

        return DashboardStatisticsResDto.ContentTypeClickStatisticsDto.builder()
                .contentTypeRatios(List.of(
                        buildContentType("BLOG", "블로그", blogCount, blogRatio),
                        buildContentType("VIDEO", "인스타그램", videoCount, videoRatio)
                ))
                .build();
    }

    private DashboardStatisticsResDto.ContentTypeRatioDto buildContentType(
            String contentType,
            String label,
            Long count,
            Integer ratio
    ) {
        return DashboardStatisticsResDto.ContentTypeRatioDto.builder()
                .contentType(contentType)
                .label(label)
                .clickCount(count)
                .ratio(ratio)
                .build();
    }
}