package com.victorbg.racofib.data.model.subject;

import com.victorbg.racofib.utils.Utils;
import com.victorbg.racofib.view.widgets.filter.SubjectFilter;

import java.util.ArrayList;
import java.util.List;

public class SubjectsFilter {

    public enum SubjectFilterOrder {
        ASCENDING,
        DESCENDING
    }

    private List<SubjectFilter> filter;
    private SubjectFilterOrder order;

    public SubjectsFilter(List<SubjectFilter> filter) {
        this(filter, SubjectFilterOrder.DESCENDING);
    }

    public SubjectsFilter(List<SubjectFilter> filter, SubjectFilterOrder order) {
        this.filter = filter;
        this.order = order;
    }

    public String getOrder() {
        return order == SubjectFilterOrder.ASCENDING ? "ASC" : "DESC";
    }

    public String getSubjects() {
        List<String> sFilter = new ArrayList<>();
        for (SubjectFilter sf : filter) {
            if (sf.checked) {
                sFilter.add(sf.subject.shortName);
            }
        }

        return Utils.getStringSubjectsApi(sFilter);
    }
}
