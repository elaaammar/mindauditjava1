# Design Document: Professional User Dashboard

## Overview

The Professional User Dashboard is a comprehensive JavaFX-based interface designed for regular users (non-administrators) of the MindAudit internal audit management system. This dashboard provides a modern, role-based user experience with real-time analytics, audit management, PDF report generation, notifications, and personalized recommendations.

### Key Design Goals

1. **Role-Based Experience**: Provide a tailored interface for users with the "User" role, distinct from administrative interfaces
2. **Data Visualization**: Present audit data through interactive charts and visual indicators for quick comprehension
3. **Self-Service Operations**: Enable users to independently manage their audits, generate reports, and configure preferences
4. **Performance**: Ensure responsive UI with asynchronous data loading and efficient database queries
5. **Extensibility**: Design modular components that can be enhanced with additional features

### Technology Stack

- **UI Framework**: JavaFX 17+ with FXML for declarative UI
- **Database**: MySQL 8.0+ with JDBC connectivity
- **Charting**: JavaFX Charts API for line, pie, and bar charts
- **PDF Generation**: Apache PDFBox 2.0+ for report generation
- **Architecture**: MVC pattern with service layer
- **Build Tool**: Maven

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Dashboard    │  │  Analytics   │  │   Reports    │      │
│  │ Controller   │  │  Controller  │  │  Controller  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Audit        │  │ Notification │  │   Report     │      │
│  │ Service      │  │  Service     │  │  Service     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ User         │  │ Recommendation│  │  Schedule    │      │
│  │ Service      │  │  Service     │  │  Service     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Database     │  │   Model      │  │   DAO        │      │
│  │ Connection   │  │   Classes    │  │  Classes     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

### Component Responsibilities

**Presentation Layer**:
- `ProfessionalDashboardController`: Main controller managing navigation and layout
- `DashboardHomeController`: Home view with overview cards and quick actions
- `AnalyticsController`: Data visualization with charts
- `MyAuditsController`: Audit CRUD operations
- `ReportsController`: Report generation and history
- `NotificationsController`: Notification display and management
- `SettingsController`: User profile and preferences

**Service Layer**:
- `AuditService`: Business logic for audit operations
- `NotificationService`: Notification creation, retrieval, and management
- `ReportService`: PDF generation and report persistence
- `RecommendationService`: Analysis and recommendation generation
- `ScheduleService`: Audit scheduling and reminder logic
- `UserService`: Extended with dashboard-specific user operations

**Data Layer**:
- `DatabaseConnection`: Connection pooling and management
- Model classes: `Audit`, `Notification`, `Report`, `Schedule`, `Recommendation`
- DAO classes for database operations

### Navigation Flow

```
Login Screen
     │
     ├─→ [User Role] → Professional Dashboard
     │                      │
     │                      ├─→ Home (default)
     │                      ├─→ My Audits
     │                      ├─→ Analytics
     │                      ├─→ Reports
     │                      ├─→ Notifications
     │                      ├─→ Settings
     │                      └─→ Logout → Login Screen
     │
     └─→ [Admin Role] → Admin Dashboard (existing)
```

## Components and Interfaces

### 1. Professional Dashboard Controller

**File**: `ProfessionalDashboardController.java`

**Responsibilities**:
- Manage main dashboard layout (header, sidebar, content area)
- Handle navigation between sections
- Maintain authentication state
- Display user identity in header
- Manage notification badge updates

**Key Methods**:
```java
public class ProfessionalDashboardController {
    void initialize()
    void setAuthService(AuthenticationService authService)
    void loadSection(String sectionName)
    void updateNotificationBadge(int count)
    void handleLogout()
    void showNotificationDropdown()
}
```

**FXML**: `professional-dashboard.fxml`
- BorderPane layout with header (top), sidebar (left), content area (center)
- Header contains: user name label, profile image, notification bell with badge, logout button
- Sidebar contains: navigation buttons for each section
- Content area: StackPane for dynamic content loading

### 2. Dashboard Home Controller

**File**: `DashboardHomeController.java`

**Responsibilities**:
- Display overview cards (global score, audit count, report count, notification count)
- Show quick summary of category statuses
- Display recent activity timeline
- Provide quick action buttons

**Key Methods**:
```java
public class DashboardHomeController {
    void initialize()
    void loadOverviewData()
    void displayGlobalScore(int score)
    void displayCategoryStatuses(Map<String, String> statuses)
    void displayRecentActivity(List<Activity> activities)
    void handleQuickAction(String actionType)
}
```

**FXML**: `dashboard-home.fxml`
- GridPane for overview cards
- HBox for category status indicators
- VBox for recent activity list
- HBox for quick action buttons

### 3. Analytics Controller

**File**: `AnalyticsController.java`

**Responsibilities**:
- Render line chart for score evolution over time
- Render pie chart for category distribution
- Render bar chart for audit comparisons
- Handle chart interactions (hover, click)
- Update charts when data changes

**Key Methods**:
```java
public class AnalyticsController {
    void initialize()
    void loadChartData()
    void renderScoreEvolutionChart(List<Audit> audits)
    void renderCategoryDistributionChart(Map<String, Integer> distribution)
    void renderAuditComparisonChart(List<Audit> audits)
    void handleChartInteraction(ChartEvent event)
}
```

**FXML**: `analytics.fxml`
- VBox container with three chart sections
- LineChart for score evolution
- PieChart for category distribution
- BarChart for audit comparison

### 4. My Audits Controller

**File**: `MyAuditsController.java`

**Responsibilities**:
- Display list of user's audits in TableView
- Handle create, read, update, delete operations
- Show audit details in modal or side panel
- Filter and sort audits
- Distinguish between completed and scheduled audits

**Key Methods**:
```java
public class MyAuditsController {
    void initialize()
    void loadAudits()
    void handleCreateAudit()
    void handleViewAudit(Audit audit)
    void handleEditAudit(Audit audit)
    void handleDeleteAudit(Audit audit)
    void showConfirmationDialog(String message)
    void refreshAuditList()
}
```

**FXML**: `my-audits.fxml`
- TableView with columns: Name, Date, Category, Score, Actions
- Button bar: Create New Audit, Refresh
- Search/filter controls

### 5. Reports Controller

**File**: `ReportsController.java`

**Responsibilities**:
- Display list of generated reports
- Trigger PDF generation for selected audit
- Open/download generated reports
- Show report generation progress

**Key Methods**:
```java
public class ReportsController {
    void initialize()
    void loadReports()
    void handleGenerateReport(Audit audit)
    void handleViewReport(Report report)
    void handleDownloadReport(Report report)
    void showProgressIndicator()
}
```

**FXML**: `reports.fxml`
- TableView with columns: Report Name, Audit, Generation Date, Actions
- Button: Generate New Report
- Progress indicator for generation

### 6. Notifications Controller

**File**: `NotificationsController.java`

**Responsibilities**:
- Display list of notifications
- Mark notifications as read
- Navigate to related content when notification is clicked
- Filter notifications by type

**Key Methods**:
```java
public class NotificationsController {
    void initialize()
    void loadNotifications()
    void handleNotificationClick(Notification notification)
    void markAsRead(Notification notification)
    void markAllAsRead()
    void filterByType(String type)
}
```

**FXML**: `notifications.fxml`
- ListView of notification items
- Button: Mark All as Read
- Filter dropdown

### 7. Settings Controller

**File**: `SettingsController.java`

**Responsibilities**:
- Display and edit user profile information
- Handle password change
- Manage notification preferences
- Toggle dark mode
- Save preferences to database

**Key Methods**:
```java
public class SettingsController {
    void initialize()
    void loadUserProfile()
    void handleSaveProfile()
    void handleChangePassword()
    void handleToggleDarkMode(boolean enabled)
    void handleNotificationPreferences(Map<String, Boolean> preferences)
    void validateProfileData()
}
```

**FXML**: `settings.fxml`
- Form for profile editing (name, email, phone)
- Password change section
- Notification preferences checkboxes
- Dark mode toggle switch

### 8. Service Classes

**AuditService**:
```java
public class AuditService {
    List<Audit> getAuditsByUserId(int userId)
    Audit getAuditById(int auditId)
    int createAudit(Audit audit)
    boolean updateAudit(Audit audit)
    boolean deleteAudit(int auditId)
    int getAuditCountByUserId(int userId)
    Audit getMostRecentAudit(int userId)
    List<Audit> getAuditsByDateRange(int userId, LocalDate start, LocalDate end)
}
```

**NotificationService**:
```java
public class NotificationService {
    List<Notification> getNotificationsByUserId(int userId)
    int getUnreadCount(int userId)
    boolean markAsRead(int notificationId)
    boolean markAllAsRead(int userId)
    int createNotification(Notification notification)
    void generateRecommendationNotifications(int userId, List<Recommendation> recommendations)
}
```

**ReportService**:
```java
public class ReportService {
    List<Report> getReportsByUserId(int userId)
    Report generatePdfReport(Audit audit)
    boolean saveReport(Report report)
    File getReportFile(int reportId)
}
```

**RecommendationService**:
```java
public class RecommendationService {
    List<Recommendation> generateRecommendations(int userId)
    List<Recommendation> getActiveRecommendations(int userId)
    boolean dismissRecommendation(int recommendationId)
}
```

**ScheduleService**:
```java
public class ScheduleService {
    List<Schedule> getScheduledAudits(int userId)
    int createSchedule(Schedule schedule)
    boolean updateSchedule(Schedule schedule)
    boolean deleteSchedule(int scheduleId)
    List<Schedule> getUpcomingReminders(int userId)
}
```

## Data Models

### Database Schema Extensions

The following tables need to be added to the existing `mindaudit` database:

```sql
-- Audit table
CREATE TABLE audit (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    global_score INT NOT NULL,
    security_score INT,
    compliance_score INT,
    performance_score INT,
    findings TEXT,
    status VARCHAR(50) DEFAULT 'completed',
    audit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_audit_date (audit_date),
    INDEX idx_category (category)
);

-- Notification table
CREATE TABLE notification (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    related_entity_type VARCHAR(50),
    related_entity_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
);

-- Report table
CREATE TABLE report (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    audit_id INT NOT NULL,
    name VARCHAR(200) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE,
    FOREIGN KEY (audit_id) REFERENCES audit(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_audit_id (audit_id)
);

-- Schedule table
CREATE TABLE schedule (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    audit_name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    scheduled_date DATE NOT NULL,
    scheduled_time TIME NOT NULL,
    reminder_sent BOOLEAN DEFAULT FALSE,
    status VARCHAR(50) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_scheduled_date (scheduled_date),
    INDEX idx_status (status)
);

-- Recommendation table
CREATE TABLE recommendation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    category VARCHAR(100) NOT NULL,
    recommendation_text TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL,
    is_dismissed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_dismissed (is_dismissed)
);

-- User preferences table
CREATE TABLE user_preferences (
    user_id INT PRIMARY KEY,
    dark_mode_enabled BOOLEAN DEFAULT FALSE,
    notification_audit_complete BOOLEAN DEFAULT TRUE,
    notification_recommendations BOOLEAN DEFAULT TRUE,
    notification_scheduled_reminders BOOLEAN DEFAULT TRUE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES userjava(id) ON DELETE CASCADE
);
```

### Java Model Classes

**Audit.java**:
```java
public class Audit {
    private int id;
    private int userId;
    private String name;
    private String category;
    private int globalScore;
    private int securityScore;
    private int complianceScore;
    private int performanceScore;
    private String findings;
    private String status;
    private LocalDateTime auditDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors, getters, setters
}
```

**Notification.java**:
```java
public class Notification {
    private int id;
    private int userId;
    private String title;
    private String message;
    private String type;
    private boolean isRead;
    private String relatedEntityType;
    private Integer relatedEntityId;
    private LocalDateTime createdAt;
    
    // Constructors, getters, setters
}
```

**Report.java**:
```java
public class Report {
    private int id;
    private int userId;
    private int auditId;
    private String name;
    private String filePath;
    private long fileSize;
    private LocalDateTime generatedAt;
    
    // Constructors, getters, setters
}
```

**Schedule.java**:
```java
public class Schedule {
    private int id;
    private int userId;
    private String auditName;
    private String category;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private boolean reminderSent;
    private String status;
    private LocalDateTime createdAt;
    
    // Constructors, getters, setters
}
```

**Recommendation.java**:
```java
public class Recommendation {
    private int id;
    private int userId;
    private String category;
    private String recommendationText;
    private String priority;
    private boolean isDismissed;
    private LocalDateTime createdAt;
    
    // Constructors, getters, setters
}
```

**UserPreferences.java**:
```java
public class UserPreferences {
    private int userId;
    private boolean darkModeEnabled;
    private boolean notificationAuditComplete;
    private boolean notificationRecommendations;
    private boolean notificationScheduledReminders;
    private LocalDateTime updatedAt;
    
    // Constructors, getters, setters
}
```

## Error Handling

### Error Categories

1. **Authentication Errors**:
   - Session expired: Redirect to login with message
   - Unauthorized access: Show access denied dialog

2. **Database Errors**:
   - Connection failure: Display retry dialog with error details
   - Query failure: Log error, show user-friendly message
   - Constraint violation: Show specific validation message

3. **Validation Errors**:
   - Invalid form data: Highlight fields, show inline error messages
   - Missing required fields: Prevent submission, show validation summary

4. **File Operation Errors**:
   - PDF generation failure: Show error dialog, log details
   - File not found: Show error message, offer to regenerate

5. **Chart Rendering Errors**:
   - Insufficient data: Show placeholder message
   - Rendering failure: Log error, show fallback message

### Error Handling Strategy

**Service Layer**:
- Throw specific exceptions: `AuditNotFoundException`, `DatabaseConnectionException`, `ValidationException`
- Log all errors with context (user ID, operation, timestamp)
- Return meaningful error messages

**Controller Layer**:
- Catch service exceptions
- Display user-friendly error dialogs
- Provide recovery options (retry, cancel, contact support)
- Never expose technical details to users

**Example Error Handling**:
```java
try {
    auditService.createAudit(audit);
    showSuccessMessage("Audit created successfully");
} catch (ValidationException e) {
    showValidationErrors(e.getErrors());
} catch (DatabaseConnectionException e) {
    showErrorDialog("Unable to connect to database. Please try again.");
    logger.error("Database connection failed", e);
} catch (Exception e) {
    showErrorDialog("An unexpected error occurred. Please contact support.");
    logger.error("Unexpected error in createAudit", e);
}
```

## Testing Strategy

### Testing Approach

This feature is **not suitable for property-based testing** because it primarily involves:
- UI rendering and layout (JavaFX components, FXML)
- Database CRUD operations with no complex transformation logic
- Side-effect operations (PDF generation, file I/O)
- Visual output (charts, styling, dark mode)

Instead, the testing strategy uses:

1. **Unit Tests**: Test service layer methods, validation logic, and utility functions
2. **Integration Tests**: Test database operations, service interactions, and data persistence
3. **UI Tests**: Test JavaFX controllers and user interactions (using TestFX)
4. **Manual Tests**: Test visual appearance, chart rendering, PDF output, and accessibility

### Unit Testing

**Focus Areas**:
- Service layer business logic
- Data validation methods
- Recommendation generation algorithm
- Date/time calculations for scheduling

**Example Tests**:
```java
@Test
void testGetAuditsByUserId_ReturnsUserAudits() {
    // Test that service returns only audits for specified user
}

@Test
void testGenerateRecommendations_LowSecurityScore_ReturnsSecurityRecommendation() {
    // Test recommendation logic for low scores
}

@Test
void testValidateAuditData_MissingName_ThrowsValidationException() {
    // Test validation logic
}
```

### Integration Testing

**Focus Areas**:
- Database CRUD operations
- Transaction handling
- Foreign key constraints
- Query performance

**Example Tests**:
```java
@Test
void testCreateAudit_ValidData_InsertsIntoDatabase() {
    // Test audit creation with database
}

@Test
void testDeleteAudit_CascadesNotifications() {
    // Test cascade delete behavior
}
```

### UI Testing (TestFX)

**Focus Areas**:
- Navigation between sections
- Form submission
- Table interactions
- Button clicks

**Example Tests**:
```java
@Test
void testNavigateToMyAudits_LoadsAuditList() {
    // Test navigation and content loading
}

@Test
void testCreateAuditButton_OpensCreateForm() {
    // Test UI interaction
}
```

### Manual Testing Checklist

- [ ] Visual appearance matches design mockups
- [ ] Charts render correctly with various data sizes
- [ ] PDF reports are properly formatted
- [ ] Dark mode applies to all components
- [ ] Notifications update in real-time
- [ ] Search functionality works across sections
- [ ] Responsive layout adapts to window resizing
- [ ] Error messages are clear and helpful
- [ ] Loading indicators appear during async operations
- [ ] Logout properly clears session

### Test Data Setup

Create test data script for development and testing:
```sql
-- Insert test audits
INSERT INTO audit (user_id, name, category, global_score, security_score, compliance_score, performance_score, findings, audit_date) VALUES
(2, 'Security Audit Q1', 'Security', 85, 90, 80, 85, 'Good security posture', '2024-01-15 10:00:00'),
(2, 'Compliance Check', 'Compliance', 72, 70, 75, 70, 'Some compliance gaps', '2024-02-20 14:30:00'),
(2, 'Performance Review', 'Performance', 65, 60, 70, 65, 'Performance needs improvement', '2024-03-10 09:15:00');

-- Insert test notifications
INSERT INTO notification (user_id, title, message, type, is_read) VALUES
(2, 'Audit Complete', 'Your Security Audit Q1 has been completed', 'audit_complete', FALSE),
(2, 'Recommendation', 'Consider improving performance metrics', 'recommendation', FALSE),
(2, 'Scheduled Reminder', 'Upcoming audit scheduled for tomorrow', 'reminder', TRUE);
```

### Performance Testing

**Metrics to Monitor**:
- Dashboard load time: < 2 seconds
- Chart rendering time: < 1 second for 100 audits
- PDF generation time: < 3 seconds per report
- Database query time: < 500ms for list operations
- Navigation transition time: < 500ms

**Load Testing**:
- Test with 1000+ audits per user
- Test with 100+ notifications
- Test concurrent user sessions

## Implementation Notes

### Phase 1: Core Infrastructure
1. Create database tables and indexes
2. Implement model classes
3. Implement service layer
4. Set up PDF generation infrastructure

### Phase 2: Basic UI
1. Create main dashboard layout
2. Implement navigation
3. Create dashboard home view
4. Implement My Audits section

### Phase 3: Advanced Features
1. Implement analytics with charts
2. Add report generation
3. Implement notification system
4. Add settings and preferences

### Phase 4: Bonus Features
1. Implement search functionality
2. Add recommendation engine
3. Implement dark mode
4. Add audit scheduling

### Dependencies

**Maven Dependencies**:
```xml
<!-- Apache PDFBox for PDF generation -->
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.29</version>
</dependency>

<!-- MySQL Connector (already present) -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- JavaFX (already present) -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>17.0.2</version>
</dependency>

<!-- TestFX for UI testing -->
<dependency>
    <groupId>org.testfx</groupId>
    <artifactId>testfx-junit5</artifactId>
    <version>4.0.16-alpha</version>
    <scope>test</scope>
</dependency>
```

### CSS Styling

Create `professional-dashboard.css` for consistent styling:
- Define color palette for light and dark modes
- Define card styles with shadows and borders
- Define button hover effects
- Define chart color schemes
- Define notification badge styles

### Security Considerations

1. **Data Access Control**: All service methods must verify user ID matches authenticated user
2. **SQL Injection Prevention**: Use PreparedStatement for all queries
3. **Session Management**: Validate session on every request
4. **Password Handling**: Never display or log passwords
5. **File Access**: Validate file paths to prevent directory traversal

### Accessibility

1. **Keyboard Navigation**: All interactive elements accessible via Tab key
2. **Focus Indicators**: Visible focus rings on all focusable elements
3. **Color Contrast**: Maintain WCAG AA contrast ratios
4. **Screen Reader Support**: Provide ARIA labels for icons and charts
5. **Text Alternatives**: Provide text descriptions for visual data

