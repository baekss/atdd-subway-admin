package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<LineStation> lineStations = new ArrayList<>();

    public Line() {}

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<LineStation> getLineStations() {
        return lineStations;
    }

    public void addLineStation(LineStation lineStation) {
        this.lineStations.add(lineStation);
        lineStation.changeLine(this);
    }

    public String getColor() {
        return color;
    }
}
