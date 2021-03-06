package com.hl.homelanebuddy;

/**
 * Created by hl0204 on 3/2/16.
 */
public final class Constants {


    public static final String TASKS_FETCHED = "TASKS_FETCHED";
    public static final String BUNDLE = "Bundle";
    public static final String DURATION = "Duration";

    public static final class Task{
        public static final String NAME = "Task";
        public static final String TASK_ID = "taskId";
        public static final String TASK_NAME = "taskName";
        public static final String TASK_TYPE = "taskType";
        public static final String TASK_ASSIGNED_TO = "assignedTo";
        public static final String TASK_DATE = "taskDate";
        public static final String TASK_STATUS = "status";

    }

    public static final class TaskStatus{
        public static final String TASK_STATUS_DONE = "taskStatusDone";
        public static final String TASK_STATUS_SLIGHT_DELAYED = "taskStatusSlightDelayed";
        public static final String TASK_STATUS_OVER_DELAYED = "taskStatusDelayed";
        public static final String TASK_STATUS_ONPROGRESS = "taskStatusOnProgress";
        public static final String TASK_STATUS_SCHEDULED_NOT_STARTED = "taskStatusNotStarted";
        public static final String TASK_STATUS_NOT_UPDATED = "taskStatusNotUpdated";
    }

    public static final String USER_REVIEW_EVENT = "USER_REVIEW_EVENT";

    public static final long DAY_1_MILLSECOND = 86400000;
    public static final long HOUR_1_MILLSECOND = 3600000;
    public static final long MINS_10_MILLSECOND = 600000;

    public static final String DURATION_1_DAY = "1 Day";
    public static final String DURATION_1_HOUR = "1 Hour";
    public static final String DURATION_10_MINS = "10 Mins";
    
    public static final String NOTIFICATION = "Notification";

    public static final String CLASS_NAME= "ClassName";
    public static final String EMAILID = "EmailId";




    public static final class APPConfig{
        public static final String review_post = "review_post";
        public static final String get_task_details = "get_task_details";

    }

}
