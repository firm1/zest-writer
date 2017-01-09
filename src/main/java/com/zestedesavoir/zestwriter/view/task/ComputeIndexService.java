package com.zestedesavoir.zestwriter.view.task;

import com.zestedesavoir.zestwriter.model.Container;
import com.zestedesavoir.zestwriter.model.MetaAttribute;
import com.zestedesavoir.zestwriter.model.Textual;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class ComputeIndexService extends Service<Map<String, Double>> {
    Function<Textual, Double> calIndex;
    Container container;

    public ComputeIndexService(Function<Textual, Double> calIndex, Container container) {
        this.calIndex = calIndex;
        this.container = container;
    }

    @Override
    protected Task<Map<String, Double>> createTask() {
        return new Task<Map<String, Double>>() {
            @Override
            protected Map<String, Double> call(){
                Map<String, Double> finalMap = new LinkedHashMap<>();
                Map<Textual, Double> statIndex = container.doOnTextual(calIndex, b -> {
                            updateMessage("Traitement de l'extrait : "+b.getTitle()+" ...");
                            return null;
                });
                for(Map.Entry<Textual, Double> st:statIndex.entrySet()) {
                    if(!(st.getKey() instanceof MetaAttribute)) {
                        finalMap.put(st.getKey().getLimitedTitle(), st.getValue());
                    } else {
                        MetaAttribute attribute = (MetaAttribute) st.getKey();
                        finalMap.put(attribute.getLimitedExpandTitle(), st.getValue());
                    }
                }
                return finalMap;
            }
        };
    }
}
