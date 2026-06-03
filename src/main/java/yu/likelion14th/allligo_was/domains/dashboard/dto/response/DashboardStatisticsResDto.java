package yu.likelion14th.allligo_was.domains.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardStatisticsResDto {

    private Long totalClicks;

    private HourlyClickStatisticsDto hourlyClickStatistics;
    private DayOfWeekClickStatisticsDto dayOfWeekClickStatistics;
    private TagClickStatisticsDto tagClickStatistics;
    private ContentTypeClickStatisticsDto contentTypeClickStatistics;

    @Getter
    @Builder
    public static class HourlyClickStatisticsDto {
        private Long averageClickCount;
        private Long maxClickCount;
        private Integer maxHour;
        private Long minClickCount;
        private Integer minHour;
        private List<HourlyClickDto> hourlyClicks;
    }

    @Getter
    @Builder
    public static class HourlyClickDto {
        private Integer hour;
        private Long clickCount;
    }

    @Getter
    @Builder
    public static class DayOfWeekClickStatisticsDto {
        private Long maxClickCount;
        private Long secondClickCount;
        private List<DayOfWeekClickDto> dayOfWeekClicks;
    }

    @Getter
    @Builder
    public static class DayOfWeekClickDto {
        private String dayOfWeek;
        private String label;
        private Long clickCount;
        private Integer rank;
    }

    @Getter
    @Builder
    public static class TagClickStatisticsDto {
        private List<TagClickRatioDto> topTagClickRatios;
    }

    @Getter
    @Builder
    public static class TagClickRatioDto {
        private String tagName;
        private Long clickCount;
        private Integer ratio;
    }

    @Getter
    @Builder
    public static class ContentTypeClickStatisticsDto {
        private List<ContentTypeRatioDto> contentTypeRatios;
    }

    @Getter
    @Builder
    public static class ContentTypeRatioDto {
        private String contentType;
        private String label;
        private Long clickCount;
        private Integer ratio;
    }
}