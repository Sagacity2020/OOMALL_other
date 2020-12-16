package cn.edu.xmu.share.model.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Stategy {
    List<Rule> rule = new ArrayList<>();
    Integer firstOrAvg;
}

