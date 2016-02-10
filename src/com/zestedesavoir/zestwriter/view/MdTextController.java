package com.zestedesavoir.zestwriter.view;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.python.util.PythonInterpreter;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.ExtractFile;

import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class MdTextController {
	private MainApp mainApp;

	PythonInterpreter pyconsole;

	@FXML
	private TabPane EditorList;

	@FXML
	private TreeView<ExtractFile> Summary;

	@FXML
	private SplitPane splitPane;

	private Map<String, Object> jsonData;
	private String baseFilePath;

	@FXML
	private void initialize() {

		loadConsolePython();
		loadFonts();

	}

	public void loadConsolePython() {
		new Thread(new Runnable() {
			public void run() {
				pyconsole = new PythonInterpreter();
				pyconsole.exec("from markdown import Markdown");
				System.out.print("1 .. ");
				pyconsole.exec("from markdown.extensions.zds import ZdsExtension");
				System.out.print("2 .. ");
				pyconsole.exec("from smileys_definition import smileys");
				System.out.print("3 .. ");
				System.out.println("PYTHON START");
			}
		}).start();
	}

	public void updateRender() {
		new Thread(new Runnable() {
			public void run() {

			}
		}).start();
	}

	public void loadFonts() {
		new Thread(new Runnable() {
			public void run() {
				Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-Regular.ttf").toExternalForm(), 10);
				Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-Black.ttf").toExternalForm(), 10);
				Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-Bold.ttf").toExternalForm(), 10);
				Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-BoldItalic.ttf").toExternalForm(), 10);
				Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-HeavyItalic.ttf").toExternalForm(), 10);
				Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-Italic.ttf").toExternalForm(), 10);
				Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-Light.ttf").toExternalForm(), 10);
				Font.loadFont(this.getClass().getResource("static/fonts/Merriweather-LightItalic.ttf").toExternalForm(), 10);

				Font.loadFont(this.getClass().getResource("static/fonts/SourceCodePro-Regular.ttf").toExternalForm(), 10);
			}
		}).start();
	}

	public MdTextController() {
		super();
	}

	public PythonInterpreter getPyconsole() {
		return pyconsole;
	}

	public SplitPane getSplitPane() {
		return splitPane;
	}

	public TreeView<ExtractFile> getSummary() {
		return Summary;
	}

	public void setPyconsole(PythonInterpreter pyconsole) {
		this.pyconsole = pyconsole;
	}

	public MainApp getMainApp() {
		return mainApp;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		mainApp.getContents().addListener(new MapChangeListener<String, String>() {

			@Override
			public void onChanged(MapChangeListener.Change change) {
				try {
					if (mainApp.getContents().containsKey("dir")) {
						openContent(mainApp.getContents().get("dir"));
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	public void createTabExtract(ExtractFile extract) throws IOException {

		extract.loadMarkdown();
		//Extract extract = new Extract(parent.getTitle().getValue(), parent.getSlug().getValue(), parent.getFilePath(), parent.getContainers());
		mainApp.getExtracts().add(extract);
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/Editor.fxml"));
		SplitPane writer = (SplitPane) loader.load();

		Tab tab = new Tab();
		tab.setText(extract.getTitle().getValue());
		tab.setContent(writer);
		EditorList.getTabs().add(tab);
		EditorList.getSelectionModel().select(tab);
		tab.setOnClosed(new EventHandler<Event>() {
			@Override
		    public void handle(Event t) {
		        mainApp.getExtracts().remove(extract);
		    }
		});

		MdConvertController controller = loader.getController();
		controller.setMdBox(this, extract, tab);
	}

	public Map<String, Object> getMapFromTreeItem(TreeItem<ExtractFile> node, Map<String, Object> map) {
		if (node.getValue().getOject().getValue() != null) {
			map.put("slug", node.getValue().getSlug().getValue());
			map.put("object", node.getValue().getOject().getValue());
			map.put("title", node.getValue().getTitle().getValue());
			if (node.getValue().isRoot()) {
				map.put("type", node.getValue().getType().getValue());
				map.put("version", node.getValue().getVersion().getValue());
				map.put("description", node.getValue().getDescription().getValue());
				map.put("licence", node.getValue().getLicence().getValue());
			}
			if (node.getValue().isContainer()) {
				map.put("introduction", node.getValue().getIntroduction().getValue());
				map.put("conclusion", node.getValue().getConclusion().getValue());
			} else {
				map.put("text", node.getValue().getText().getValue());
			}

			List<Map<String, Object>> tabs = new ArrayList<>();
			for(TreeItem<ExtractFile> child:node.getChildren()) {
				Map<String, Object> h = getMapFromTreeItem(child, new HashMap<String, Object>());
				if(h != null) {
					tabs.add(h);
				}
			}

			if (tabs.size() > 0) {
				map.put("children", tabs);
			}
			return map;
		}
		return null;
	}

	public TreeItem<ExtractFile> addChild(TreeItem<ExtractFile> node, Map container, String path) {
		if (container.get("object").equals("container")) {
			node.getValue().setConclusion(container.get("conclusion").toString());
			node.getValue().setIntroduction(container.get("introduction").toString());
			if (container.containsKey("introduction")) {
				TreeItem<ExtractFile> itemIntro = new TreeItem<ExtractFile>(
						new ExtractFile("Introduction",
										container.get("slug").toString(),
										baseFilePath,
										container.get("introduction").toString(),
										null));
				node.getChildren().add(itemIntro);
			}
			if (container.containsKey("children")) {
				List children = (ArrayList) container.get("children");
				String intro_path = container.get("introduction").toString();
				String buildPath = baseFilePath + File.separator + intro_path.substring(0, intro_path.length() - 15);
				for (Object child : children) {
					Map childMap = (Map) child;
					TreeItem<ExtractFile> item = new TreeItem<ExtractFile>(
							new ExtractFile(
									childMap.get("title").toString(),
									childMap.get("slug").toString(),
									baseFilePath,
									"",
									""));
					node.getChildren().add(addChild(item, childMap, path));
				}
			}
			if (container.containsKey("conclusion")) {
				TreeItem<ExtractFile> itemConclu = new TreeItem<ExtractFile>(
						new ExtractFile("Conclusion",
								container.get("slug").toString(),
								baseFilePath,
								container.get("conclusion").toString(),
								null));
				node.getChildren().add(itemConclu);
			}
			return node;
		} else {
			if (container.get("object").equals("extract")) {
				TreeItem<ExtractFile> item = new TreeItem<ExtractFile>(
						new ExtractFile(
								container.get("title").toString(),
								container.get("slug").toString(),
								baseFilePath,
								container.get("text").toString()));
				return item;
			}
		}
		return null;

	}

	public void openContent(String filePath) throws JsonParseException, JsonMappingException, IOException {

		this.baseFilePath = filePath;
		ObjectMapper mapper = new ObjectMapper();
		jsonData = mapper.readValue(new File(filePath + File.separator + "manifest.json"), Map.class);

		// load content informations
		String contentTitle = jsonData.get("title").toString();
		String contentSlug = jsonData.get("slug").toString();
		mainApp.getZdsutils().setLocalSlug(contentSlug);
		mainApp.getZdsutils().setLocalType(jsonData.get("type").toString().toLowerCase());
		TreeItem<ExtractFile> rootItem = new TreeItem<ExtractFile>(
				new ExtractFile(
						contentTitle,
						contentSlug,
						baseFilePath,
						jsonData.get("version").toString(),
						jsonData.get("description").toString(),
						jsonData.get("type").toString(),
						jsonData.get("licence").toString(),
						jsonData.get("introduction").toString(),
						jsonData.get("conclusion").toString()));
		rootItem.setExpanded(true);
		Summary.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		rootItem = addChild(rootItem, jsonData, filePath);
		Summary.setRoot(rootItem);
	    Summary.setCellFactory(new Callback<TreeView<ExtractFile>, TreeCell<ExtractFile>>() {

	        @Override
	        public TreeCell<ExtractFile> call(TreeView<ExtractFile> extractTreeView) {
	            TreeCell<ExtractFile> treeCell = new TreeCell<ExtractFile>() {
	            	private TextField textField;
	            	private final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	            	private final Pattern WHITESPACE = Pattern.compile("[\\s]");

	            	public String toSlug(String input) {
	            	    String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
	            	    String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
	            	    String slug = NONLATIN.matcher(normalized).replaceAll("");
	            	    return slug.toLowerCase(Locale.ENGLISH);
	            	}

	            	private ContextMenu addMenu = new ContextMenu();

	            	public void initContextMenu(ExtractFile item) {
	                    MenuItem addMenuItem1 = new MenuItem("Ajouter un extrait", new ImageView(new Image(this.getClass().getResourceAsStream("static/icons/child.png"), 16, 16, true, true)));
	                    MenuItem addMenuItem2 = new MenuItem("Ajouter un conteneur", new ImageView(new Image(this.getClass().getResourceAsStream("static/icons/container.png"), 16, 16, true, true)));
	                    MenuItem addMenuItem3 = new MenuItem("Editer", new ImageView(new Image(this.getClass().getResourceAsStream("static/icons/editor.png"), 16, 16, true, true)));
	                    MenuItem addMenuItem4 = new MenuItem("Supprimer", new ImageView(new Image(this.getClass().getResourceAsStream("static/icons/delete.png"), 16, 16, true, true)));
	                    addMenu.getItems().clear();
	                    if (item.canTakeContainer(getAncestorContainerCount(getTreeItem()))) {
	                    	addMenu.getItems().add(addMenuItem2);
	                    }
	                    if (item.canTakeExtract()) {
	                    	addMenu.getItems().add(addMenuItem1);
	                    }
	                    if (item.canEdit()) {
	                    	addMenu.getItems().add(addMenuItem3);
	                    }
	                    if (item.canDelete()) {
		                    addMenu.getItems().add(new SeparatorMenuItem());
		                    addMenu.getItems().add(addMenuItem4);
	                    }

	                    addMenuItem4.setOnAction(new EventHandler<ActionEvent>() {
	                        public void handle(ActionEvent t) {
	                        	Alert alert = new Alert(AlertType.CONFIRMATION);
	                        	alert.setTitle("Confirmation de suppression");
	                        	alert.setHeaderText(null);
	                        	alert.setContentText("Êtes vous sur de vouloir supprimer ?");

	                        	Optional<ButtonType> result = alert.showAndWait();
	                        	if (result.get() == ButtonType.OK){
	                        	    getTreeItem().getParent().getChildren().remove(getTreeItem());
	                        	    saveManifestJson();
	                        	}
	                        }
	                    });

	                    addMenuItem1.setOnAction(new EventHandler<ActionEvent>() {
	                        public void handle(ActionEvent t) {
	                        	TextInputDialog dialog = new TextInputDialog("Extrait");
	                        	ExtractFile extract;
	                        	dialog.setTitle("Nouvel extrait");
	                        	dialog.setHeaderText(null);
	                        	dialog.setContentText("Titre de l'extrait:");

	                        	Optional<String> result = dialog.showAndWait();
	                        	if (result.isPresent()){
	                        		extract = new ExtractFile(
	                        	    		result.get(),
		                        			toSlug(result.get()),
		                        			baseFilePath,
		                        			(getItem().getFilePath()+File.separator+toSlug(result.get())+".md").substring(baseFilePath.length()));
	                        		TreeItem<ExtractFile> newChild = new TreeItem<ExtractFile>(extract);
		                            getTreeItem().getChildren().add(newChild);
		                            // create file
		                            File extFile = new File(extract.getFilePath());
		                            if(!extFile.exists()) {
		                            	try {
											extFile.createNewFile();
										} catch (IOException e) {
											e.printStackTrace();
										}
		                            }
		                            saveManifestJson();
	                        	}
	                        }
	                    });

	                    addMenuItem2.setOnAction(new EventHandler<ActionEvent>() {
	                        public void handle(ActionEvent t) {
	                        	TextInputDialog dialog = new TextInputDialog("Conteneur");

	                        	dialog.setTitle("Nouveau conteneur");
	                        	dialog.setHeaderText(null);
	                        	dialog.setContentText("Titre du conteneur:");

	                        	Optional<String> result = dialog.showAndWait();
	                        	if (result.isPresent()){
	                        		ExtractFile extract = new ExtractFile(
	                        	    		result.get(),
		                        			toSlug(result.get()),
		                        			baseFilePath,
		                        			(getItem().getFilePath()+File.separator+toSlug(result.get())+File.separator+"introduction.md").substring(baseFilePath.length()),
		                        			(getItem().getFilePath()+File.separator+toSlug(result.get())+File.separator+"conclusion.md").substring(baseFilePath.length()));
	                        		TreeItem<ExtractFile> newChild = new TreeItem<ExtractFile>(extract);
	                        		ExtractFile extIntro = new ExtractFile(
	                        	    		"Introduction",
	                        	    		toSlug(result.get()),
		                        			baseFilePath,
		                        			(getItem().getFilePath()+File.separator+toSlug(result.get())+File.separator+"introduction.md").substring(baseFilePath.length()),
		                        			null);
	                        		TreeItem<ExtractFile> newChildIntro = new TreeItem<ExtractFile>(extIntro);
	                        		ExtractFile extConclu = new ExtractFile(
	                        	    		"Conclusion",
	                        	    		toSlug(result.get()),
		                        			baseFilePath,
		                        			null,
		                        			(getItem().getFilePath()+File.separator+toSlug(result.get())+File.separator+"conclusion.md").substring(baseFilePath.length()));
	                        		TreeItem<ExtractFile> newChildConclu = new TreeItem<ExtractFile>(extConclu);
	                        		newChild.getChildren().add(newChildIntro);
	                        		newChild.getChildren().add(newChildConclu);
		                            getTreeItem().getChildren().add(getTreeItem().getChildren().size()-1, newChild);
		                            // create files
		                            File dirFile = new File(extract.getFilePath());
		                            File introFile = new File(extIntro.getFilePath());
		                            File concluFile = new File(extConclu.getFilePath());

		                            if(!dirFile.exists() && !dirFile.isDirectory()) {
		                            	dirFile.mkdir();
		                            }
		                            if(!introFile.exists()) {
		                            	try {
											introFile.createNewFile();
										} catch (IOException e) {
											e.printStackTrace();
										}
		                            }
		                            if(!concluFile.exists()) {
		                            	try {
		                            		concluFile.createNewFile();
										} catch (IOException e) {
											e.printStackTrace();
										}
		                            }
		                            saveManifestJson();
	                        	}
	                        }
	                    });

	                    addMenuItem3.setOnAction(new EventHandler<ActionEvent>() {
	                        public void handle(ActionEvent t) {
			                    TreeItem<ExtractFile> item = Summary.getSelectionModel().getSelectedItem();
								if ((!mainApp.getExtracts().contains(item.getValue()))
										&& (item.getValue().getFilePath() != null)) {
									try {
										createTabExtract(item.getValue());
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
	                        }
	                    });
	                }

	            	@Override
	    	        public void startEdit() {
	    	            super.startEdit();

	    	            if (getItem().getOject() != null && getItem().isEditable()) {
		    	            if (textField == null) {
		    	                createTextField();
		    	            }
		    	            setText(null);
		    	            setGraphic(textField);
		    	            textField.selectAll();
	    	            }
	    	        }

	            	@Override
	                public void cancelEdit() {
	                    super.cancelEdit();
	                    setText(getString());
	                    setGraphic(getTreeItem().getGraphic());
	                }

	                protected void updateItem(ExtractFile item, boolean empty) {
	                    super.updateItem(item, empty);

	                    if (empty) {
	                        setText(null);
	                        setGraphic(null);
	                    } else {
	                        if (isEditing()) {
	                            if (textField != null) {
	                                textField.setText(getString());
	                            }
	                            setText(null);
	                            setGraphic(textField);
	                        } else {
	                            setText(getString());
	                            if(getItem().isContainer()) {
	                            	setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream("static/icons/container.png"), 20, 20, true, true)));
	                            }
	                            else {
	                            	setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream("static/icons/child.png"), 20, 20, true, true)));
	                            }
	                            setContextMenu(addMenu);
	                        }
	                        initContextMenu(item);
	                    }
	                }

	                private void createTextField() {
	                    textField = new TextField(getString());
	                    textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

	                        @Override
	                        public void handle(KeyEvent t) {
	                            if (t.getCode() == KeyCode.ENTER) {
	                            	ExtractFile extract = getItem();
	                            	extract.setTitle(textField.getText());
	                                commitEdit(extract);
	                                saveManifestJson();
	                            } else if (t.getCode() == KeyCode.ESCAPE) {
	                                cancelEdit();
	                            }
	                        }
	                    });
	                }

	                private String getString() {
	                    return getItem() == null ? "" : getItem().getTitle().getValue();
	                }
	            };

	            treeCell.setOnDragDetected(new EventHandler<MouseEvent>() {
	                @Override
	                public void handle(MouseEvent mouseEvent) {
	                    if (treeCell.getItem() == null) {
	                        return;
	                    }
	                    Dragboard dragBoard = treeCell.startDragAndDrop(TransferMode.MOVE);
	                    ClipboardContent content = new ClipboardContent();
	                    content.put(DataFormat.PLAIN_TEXT, treeCell.getTreeItem().toString());
	                    dragBoard.setContent(content);
	                    mouseEvent.consume();
	                }
	            });

	            treeCell.setOnDragDone(new EventHandler<DragEvent>() {
	                @Override
	                public void handle(DragEvent dragEvent) {
	                    dragEvent.consume();
	                }
	            });

	            treeCell.setOnDragExited(new EventHandler<DragEvent>() {
	                @Override
	                public void handle(DragEvent dragEvent) {
	                	treeCell.setGraphic(null);
	                    dragEvent.consume();
	                }
	            });

	            treeCell.setOnDragOver(new EventHandler<DragEvent>() {
	                @Override
	                public void handle(DragEvent dragEvent) {
	                	String valueToMove = dragEvent.getDragboard().getString();
	                    TreeItem<ExtractFile> itemToMove = search(Summary.getRoot(), valueToMove);
	                    TreeItem<ExtractFile> newParent = treeCell.getTreeItem();
	                    if(!itemToMove.getValue().isMoveableIn(treeCell.getItem(), getDescendantContainerCount(itemToMove)+getAncestorContainerCount(treeCell.getTreeItem()))) {
	                    	treeCell.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream("static/icons/delete.png"))));
	                    }
	                    else {
	                    	dragEvent.acceptTransferModes(TransferMode.MOVE);
	                    }
	                    dragEvent.consume();
	                }
	            });

	            treeCell.setOnDragDropped(new EventHandler<DragEvent>() {
	                @Override
	                public void handle(DragEvent dragEvent) {
	                    String valueToMove = dragEvent.getDragboard().getString();
	                    TreeItem<ExtractFile> itemToMove = search(Summary.getRoot(), valueToMove);
	                    TreeItem<ExtractFile> newParent = treeCell.getTreeItem();
	                    // Remove from former parent.
	                    itemToMove.getParent().getChildren().remove(itemToMove);

	                    if(newParent.getValue().isContainer()) {
	                    	int position = newParent.getChildren().size();
		                    // Add to new parent.
		                    newParent.getChildren().add(position-1, itemToMove);
	                    } else {
	                    	//if(oldParent.equals(newParent.getParent())) {
	                    		int position = newParent.getParent().getChildren().indexOf(newParent);
			                    // Add after new item.
			                    newParent.getParent().getChildren().add(position+1, itemToMove);
	                    	//}
	                    }

	                    newParent.setExpanded(true);
	                    dragEvent.consume();

	                    // save json file
	                    saveManifestJson();
	                }
	            });

	            return treeCell;
	        }

	        private TreeItem<ExtractFile> search(final TreeItem<ExtractFile> currentNode, final String valueToSearch) {
	            TreeItem<ExtractFile> result = null;
	            if (currentNode.toString().equals(valueToSearch)) {
	                result = currentNode;
	            } else if (!currentNode.isLeaf()) {
	                for (TreeItem<ExtractFile> child : currentNode.getChildren()) {
	                    result = search(child, valueToSearch);
	                    if (result != null) {
	                        break;
	                    }
	                }
	            }
	            return result;
	        }
	    });
	}

	public static int getAncestorContainerCount(TreeItem<ExtractFile> node) {
		if(node.getParent()!=null) {
			return getAncestorContainerCount(node.getParent()) + 1;
		}
		else {
			return 1;
		}
	}
	public static int getDescendantContainerCount(TreeItem<ExtractFile> node) {
		int maxDepth=0;
		for(TreeItem<ExtractFile> n: node.getChildren()) {
			if (n.getValue().isContainer()) {
				maxDepth = Math.max(maxDepth, getDescendantContainerCount(n) + 1);
			}
		}
		return maxDepth;
	}

	public void saveManifestJson() {
        Map<String, Object> res = getMapFromTreeItem(Summary.getRoot(), new HashMap<String, Object>());
        ObjectMapper mapper = new ObjectMapper();
        try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(new File(baseFilePath+File.separator+"manifest.json"), res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
