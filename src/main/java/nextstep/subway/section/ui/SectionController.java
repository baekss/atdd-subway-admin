package nextstep.subway.section.ui;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.domain.LineStations;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.net.URI;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lines")
@Slf4j
public class SectionController {
    private final SectionService sectionService;
    private final LineService lineService;
    private final StationService stationService;

    public SectionController(final SectionService sectionService, final LineService lineService, final StationService stationService) {
        this.sectionService = sectionService;
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping(value = "/{lineId}/sections")
    public ResponseEntity<StationResponse> createSection(@PathVariable("lineId") Long lineId, @RequestBody SectionRequest sectionRequest) {
        Line line = lineService.findLineAndLineStations(lineId);
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());

        LineStation lineStation = sectionService.addNewLineStation(line,
                new LineStations(line.getLineStations()),
                upStation,
                downStation,
                new Distance(sectionRequest.getDistance()));
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections")).body(StationResponse.of(lineStation.getStation()));
    }

    @DeleteMapping(value = "/{lineId}/sections")
    public ResponseEntity<Void> removeSection(@PathVariable("lineId") Long lineId, @RequestParam("stationId") Long stationId) {
        Line line = lineService.findLine(lineId);
        Station station = stationService.findStationById(stationId);
        sectionService.removeSection(line, station);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
        log.info("log >>> " + e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Void> handleRuntimeException(RuntimeException e) {
        log.info("log >>> " + e.getMessage());
        return ResponseEntity.badRequest().build();
    }
}
