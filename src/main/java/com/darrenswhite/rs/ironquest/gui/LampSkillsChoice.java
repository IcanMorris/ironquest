package com.darrenswhite.rs.ironquest.gui;

import com.darrenswhite.rs.ironquest.IronQuest;
import com.darrenswhite.rs.ironquest.player.Skill;
import java.util.Objects;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Darren White
 */
public class LampSkillsChoice extends Stage {

  /**
   * The logger
   */
  private static final Logger LOG = LogManager.getLogger(QuestDetail.class);

  /**
   * The title of the window
   */
  private static final String STAGE_NAME = "Skill Lamp Choices";

  /**
   * The GridPane which contains all components
   */
  private GridPane grid;

  /**
   * ScrollPane which wraps the GridPane
   */
  private ScrollPane scroll;

  /**
   * The Scene for this Stage
   */
  private Scene scene;

  /**
   * The Skills ListView
   */
  private ListView<Skill> listSkills;

  /**
   * The list of Skills
   */
  private ObservableList<Skill> skills;

  /**
   * The skills label
   */
  private Label lblSkills;

  /**
   * Creates a new LampSkillsChoice Stage
   *
   * @param owner The parent Window
   */
  LampSkillsChoice(Window owner) {
    Objects.requireNonNull(owner);

    // Use window modality so the main window cannot be used while
    // this window is open
    initModality(Modality.WINDOW_MODAL);
    // Set the owner as the main window
    initOwner(owner);
    // No need for the task details to be resizable
    setResizable(false);
    // Set the title of the window
    setTitle(STAGE_NAME);

    init();
  }

  private void init() {
    grid = new GridPane();
    // Set padding & gaps to 10px
    grid.setPadding(new Insets(10));
    grid.setHgap(10);
    grid.setVgap(10);

    // Wrap GridPane in ScrollPane
    scroll = new ScrollPane(grid);
    // Set the maximum size that the scrollpane should be
    scroll.setMaxSize(500, 500);
    // Don't display horizontal scrollbar as we don't need it
    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    // Create a new Scene containing the ScrollPane
    scene = new Scene(scroll);

    skills = FXCollections.observableArrayList(Skill.values());
    listSkills = new ListView<>(skills);

    // Allow multiple selections
    listSkills.getSelectionModel()
        .setSelectionMode(SelectionMode.MULTIPLE);
    // Add change listener to update Skill Lamp choices
    listSkills.getSelectionModel().getSelectedItems()
        .addListener(this::updateLampSkills);

    lblSkills = new Label();

    grid.add(lblSkills, 0, 0);
    // Add components to the grid
    grid.add(listSkills, 0, 1);

    // Select current Lamp Skills
    selectSkills();

    // Set the scene
    sizeToScene();
    setScene(scene);
  }

  /**
   * Selects the current lamp skills
   */
  private void selectSkills() {
    Set<Skill> skills = IronQuest.getInstance().getLampSkills();

    for (Skill s : skills) {
      listSkills.getSelectionModel().select(s);
    }

    updateSkillsLabel(skills);
  }

  /**
   * Updates the Lamp Skills choices
   *
   * @param c The Change event
   */
  private void updateLampSkills(ListChangeListener.Change<? extends Skill> c) {
    IronQuest quest = IronQuest.getInstance();
    // Get current Lamp Skills
    Set<Skill> skills = quest.getLampSkills();

    // Process all updates
    while (c.next()) {
      if (c.wasUpdated()) {
        LOG.debug("LampForceSkills: Updated");
      } else if (c.wasPermutated()) {
        LOG.debug("LampForceSkills: Permutated");
      } else {
        try {
          // Add all added skills
          if (c.wasAdded()) {
            skills.addAll(c.getAddedSubList());
          }

          // Remove all removed skills
          if (c.wasRemoved()) {
            skills.removeAll(c.getRemoved());
          }
        } catch (IndexOutOfBoundsException ignored) {
          // There is a bug in Java 8
          // See https://bugs.openjdk.java.net/browse/JDK-8138843
          skills.clear();
          skills.addAll(c.getList());
        }
      }
    }

    updateSkillsLabel(skills);

    // Update the Lamp Skills Set
    IronQuest.getInstance().setLampSkills(skills);
  }

  /**
   * Updates the Skills label
   *
   * @param skills The Set of Skills currently selected
   */
  private void updateSkillsLabel(Set<Skill> skills) {
    StringBuilder sb = new StringBuilder();
    int index = 1;

    for (Skill skill : skills) {
      sb.append(index++).append(". ").append(skill.toString()).append('\n');
    }

    if (sb.length() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }

    lblSkills.setText(sb.toString());
  }
}
