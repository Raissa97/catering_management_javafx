package businesslogic;

import businesslogic.event.EventManager;
import businesslogic.menu.MenuManager;
import businesslogic.recipe.RecipeManager;
import businesslogic.task.TaskManager;
import businesslogic.user.UserManager;
import persistence.EventPersistence;
import persistence.MenuPersistence;
import persistence.TaskPersistence;

public class CatERing {
    private static CatERing singleInstance;

    public static CatERing getInstance() {
        if (singleInstance == null) {
            singleInstance = new CatERing();
        }
        return singleInstance;
    }

    private MenuManager menuMgr;
    private RecipeManager recipeMgr;
    private UserManager userMgr;
    private EventManager eventMgr;
    private TaskManager taskMgr;

    private MenuPersistence menuPersistence;
    private EventPersistence eventPersistence;
    private TaskPersistence taskPersistence;

    private CatERing() {
        menuMgr = new MenuManager();
        menuPersistence = new MenuPersistence();
        menuMgr.addEventReceiver(menuPersistence);

        eventMgr = new EventManager();
        eventPersistence = new EventPersistence();
        eventMgr.addEventReceiver(eventPersistence);

        taskMgr = new TaskManager();
        taskPersistence = new TaskPersistence();
        taskMgr.addEventReceiver(taskPersistence);

        userMgr = new UserManager();
        recipeMgr = new RecipeManager();
    }


    public MenuManager getMenuManager() {
        return menuMgr;
    }

    public RecipeManager getRecipeManager() {
        return recipeMgr;
    }

    public UserManager getUserManager() { return userMgr; }

    public EventManager getEventManager() { return eventMgr; }

    public TaskManager getTaskManager() { return taskMgr; }
}
