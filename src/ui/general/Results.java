package ui.general;

import businesslogic.task.Task;
import businesslogic.task.TaskAssignment;

import java.sql.Time;
import java.time.LocalDate;
import java.sql.Date;

public class Results {
    public String text;
    public Date inDate;
    public Date endDate;

    public java.sql.Date date;
    public Time timeInizio;
    public Time timeFine;
    public String people;
    public int pp;
    public String luogo;
    public boolean isRic;
    public int ogniQuanto;
    public int perQuanto;
    public Date dataFineRip;

    public Task task;
    public TaskAssignment taskAssignment;
    public String timeTask;
    public String porzTask;
    public String descTask;
    public String cookOrService;
    public int workshift_id;

    //creazione evento
    public Results(String name, Date inD, Date endD, String luogo, int pp, boolean ric) {
        this.text = name;
        this.inDate = inD;
        this.endDate = endD;
        this.pp = pp;
        this.luogo = luogo;
        this.isRic = ric;
    }

    //creazione schede evento
    public Results(String name, Date inD, Date endD, String pp) {
        this.text = name;
        this.inDate = inD;
        this.endDate = endD;
        this.people=pp;
    }

    //edit scheda evento
    public Results(String name, String place, int part, Date inD, Date endD) {
        this.text = name;
        this.inDate = inD;
        this.endDate = endD;
        this.luogo=place;
        this.pp=part;
    }

    //creazione scheda evento
    public Results(Date inD, Date endD, int pp, String luogo) {
        this.inDate = inD;
        this.endDate = endD;
        this.pp=pp;
        this.luogo = luogo;
    }

    //per creazione di evento ricorrente
    public Results(int perQ, int ogniQ, Date dataFineRip){
        this.perQuanto = perQ;
        this.ogniQuanto = ogniQ;
        this.dataFineRip = dataFineRip;
    }

    // creazione servizio
    public Results(String name, java.sql.Date data, Time timeI, Time timeF, String pp) {
        this.text = name;
        this.date = data;
        this.timeInizio = timeI;
        this.timeFine = timeF;
        this.people=pp;
    }

    /* per task creazione e edit */
    public Results(Task t, TaskAssignment ta, String desc, String time, String portions, String cookName, int turno){
        this.task = t;
        this.taskAssignment = ta;
        this.descTask=desc;
        this.timeTask=time;
        this.cookOrService=cookName;
        this.workshift_id=turno;
        this.porzTask=portions;
    }

    public Results(String desc){
        this.descTask = desc;
    }
}
