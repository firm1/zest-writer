package com.zestedesavoir.zestwriter.view.com;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.zestedesavoir.zestwriter.model.ExtractFile;
import com.zestedesavoir.zestwriter.view.dialogs.EditContentDialog;

import javafx.scene.control.TreeItem;
import javafx.util.Pair;

public class FunctionTreeFactory {
	@FunctionalInterface
    interface Function<A, B, R> {
    	public R apply(A a, B b);
    }

    public static int countFromTree(TreeItem<ExtractFile> node, Function<TreeItem<ExtractFile>, Integer, Integer> f) {
    	int sum =0;
        for(TreeItem<ExtractFile> item:node.getChildren()) {
            sum += f.apply(item, sum);
        }
        return sum;
    }

    public static int getDirectChildCount(TreeItem<ExtractFile> node) {
        return countFromTree(node, (item, sum) -> {
            if(!item.getValue().isContainer()) {
                sum++;
            }
            return sum;
        });
    }

    /*
     * Count container descendants of TreeItem node
     * List all children of node and count recursively any child which are container
     */
    public static int getDescendantContainerCount(TreeItem<ExtractFile> node) {
    	return countFromTree(node, (item, sum) -> {
    		if (item.getValue().isContainer()) {
                sum = Math.max(sum, getDescendantContainerCount(item) + 1);
            }
    		return sum;
    	});
    }

    public static int getAncestorContainerCount(TreeItem<ExtractFile> node) {
        if (node.getParent() != null) {
            return getAncestorContainerCount(node.getParent()) + 1;
        } else {
            return 1;
        }
    }

    public static Map<String,Object> initContentDialog(Map<String, Object> defaultParam) {
        if(defaultParam == null) {
            defaultParam = new HashMap<>();
            defaultParam.put("title", "");
            defaultParam.put("description", "");
            defaultParam.put("type", EditContentDialog.typeOptions.get(1));
            defaultParam.put("licence", EditContentDialog.licOptions.get(6));
        }
        // Create wizard
        EditContentDialog dialog = new EditContentDialog(defaultParam);

        Optional<Pair<String, Map<String, Object>>> result = dialog.showAndWait();
        if(result.isPresent()) {
            return result.get().getValue();
        } else {
            return null;
        }

     }
}
