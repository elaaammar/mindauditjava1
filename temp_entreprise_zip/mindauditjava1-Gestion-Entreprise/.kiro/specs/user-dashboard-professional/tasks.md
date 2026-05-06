# Implementation Plan: Professional User Dashboard

## Overview

This implementation plan breaks down the Professional User Dashboard feature into discrete, actionable coding tasks. The plan follows a four-phase approach: Core Infrastructure, Basic UI, Advanced Features, and Bonus Features. Each task builds incrementally on previous work, with checkpoints to validate progress.

## Tasks


### Phase 1: Core Infrastructure
- [x] 1. Set up database schema and tables
  - Create SQL migration script with all new tables (audit, notification, report, schedule, recommendation, user_preferences)
  - Add indexes for performance optimization
  - Add foreign key constraints with cascade rules
  - Execute migration script and verify table creation
  - _Requirements: 17.1, 17.4_

- [x] 2. Create Java model classes for new entities
  - [x] 2.1 Create Audit.java model class
    - Define all fields matching database schema
    - Add constructors, getters, and setters
    - Add validation annotations if using Bean Validation
    - _Requirements: 5.1, 5.2_
  
  - [x] 2.2 Create Notification.java model class
    - Define all fields matching database schema
    - Add constructors, getters, and setters
    - _Requirements: 7.1, 7.2, 7.3_
  
  - [x] 2.3 Create Report.java model class
    - Define all fields matching database schema
    - Add constructors, getters, and setters
    - _Requirements: 6.1, 6.5_
  
  - [x] 2.4 Create Schedule.java model class
    - Define all fields matching database schema
    - Add constructors, getters, and setters
    - _Requirements: 12.1, 12.4_
  
  - [x] 2.5 Create Recommendation.java model class
    - Define all fields matching database schema
    - Add constructors, getters, and setters
    - _Requirements: 10.1, 10.3_
  
  - [x] 2.6 Create UserPreferences.java model class
    - Define all fields matching database schema
    - Add constructors, getters, and setters
    - _Requirements: 8.7, 8.9, 11.1_

- [x] 3. Implement AuditService with core CRUD operations
  - [x] 3.1 Create AuditService.java class
    - Implement getAuditsByUserId() method with PreparedStatement
    - Implement getAuditById() method
    - Implement createAudit() method with validation
    - Implement updateAudit() method
    - Implement deleteAudit() method
    - Implement getAuditCountByUserId() method
    - Implement getMostRecentAudit() method
    - Implement getAuditsByDateRange() method
    - _Requirements: 5.1, 5.2, 5.9, 5.10, 14.3_
  
  - [ ]* 3.2 Write unit tests for AuditService
    - Test CRUD operations with mock database
    - Test user ID filtering to ensure data isolation
    - Test validation logic for invalid audit data
    - Test edge cases (empty results, null values)
    - _Requirements: 5.1, 5.9, 14.3_

- [x] 4. Implement NotificationService
  - [x] 4.1 Create NotificationService.java class
    - Implement getNotificationsByUserId() method
    - Implement getUnreadCount() method
    - Implement markAsRead() method
    - Implement markAllAsRead() method
    - Implement createNotification() method
    - Implement generateRecommendationNotifications() method
    - _Requirements: 7.1, 7.2, 7.3, 7.6, 7.7, 7.8, 7.9_
  
  - [ ]* 4.2 Write unit tests for NotificationService
    - Test notification retrieval and filtering
    - Test unread count calculation
    - Test mark as read functionality
    - _Requirements: 7.6, 7.7, 7.8_

- [x] 5. Implement ReportService with PDF generation
  - [x] 5.1 Add Apache PDFBox dependency to pom.xml
    - Add PDFBox dependency version 2.0.29
    - Update Maven dependencies
    - _Requirements: 6.1, 6.2_
  
  - [x] 5.2 Create ReportService.java class
    - Implement getReportsByUserId() method
    - Implement generatePdfReport() method using PDFBox
    - Implement saveReport() method to persist report metadata
    - Implement getReportFile() method to retrieve report files
    - Create PDF template with headers, tables, and formatting
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 6.8_
  
  - [ ]* 5.3 Write unit tests for ReportService
    - Test report metadata persistence
    - Test PDF file creation (verify file exists and is valid PDF)
    - Test report retrieval by user ID
    - _Requirements: 6.1, 6.5_

- [ ] 6. Checkpoint - Verify core infrastructure
  - Run all unit tests and ensure they pass
  - Verify database tables are created correctly
  - Test service methods manually with sample data
  - Ask the user if questions arise

### Phase 2: Basic UI

- [ ] 7. Create main dashboard layout and navigation
  - [x] 7.1 Create professional-dashboard.fxml
    - Design BorderPane layout with header (top), sidebar (left), content area (center)
    - Add header elements: user name label, profile image, notification bell with badge, logout button
    - Add sidebar navigation buttons: Home, My Audits, Analytics, Reports, Notifications, Settings
    - Add StackPane for dynamic content loading in center
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 13.1, 13.2, 13.3_
  
  - [x] 7.2 Create ProfessionalDashboardController.java
    - Implement initialize() method to set up UI components
    - Implement setAuthService() method to inject authentication service
    - Implement loadSection() method to dynamically load FXML content
    - Implement updateNotificationBadge() method
    - Implement handleLogout() method
    - Implement showNotificationDropdown() method
    - Add navigation button event handlers
    - _Requirements: 1.1, 1.3, 2.6, 13.3, 13.4_
  
  - [ ]* 7.3 Write UI tests for navigation
    - Test navigation between sections using TestFX
    - Test logout functionality
    - Test notification badge updates
    - _Requirements: 13.3, 13.4_

- [ ] 8. Create dashboard home view
  - [x] 8.1 Create dashboard-home.fxml
    - Design GridPane for overview cards (global score, audit count, report count, notification count)
    - Add HBox for category status indicators
    - Add VBox for recent activity timeline
    - Add HBox for quick action buttons
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.6_
  
  - [x] 8.2 Create DashboardHomeController.java
    - Implement initialize() method
    - Implement loadOverviewData() method to fetch user statistics
    - Implement displayGlobalScore() method with visual indicators
    - Implement displayCategoryStatuses() method
    - Implement displayRecentActivity() method
    - Implement handleQuickAction() method for quick action buttons
    - Handle case when no audits exist (welcome message)
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_
  
  - [ ]* 8.3 Write unit tests for DashboardHomeController
    - Test data loading and display logic
    - Test welcome message display when no audits exist
    - Test quick action button handlers
    - _Requirements: 3.5_

- [ ] 9. Create My Audits section with CRUD operations
  - [ ] 9.1 Create my-audits.fxml
    - Design TableView with columns: Name, Date, Category, Score, Actions
    - Add button bar with "Create New Audit" and "Refresh" buttons
    - Add search/filter controls
    - Add modal or side panel for audit details
    - _Requirements: 5.1, 5.2, 5.10, 5.11_
  
  - [ ] 9.2 Create MyAuditsController.java
    - Implement initialize() method to set up TableView
    - Implement loadAudits() method to populate table
    - Implement handleCreateAudit() method to open create form
    - Implement handleViewAudit() method to display audit details
    - Implement handleEditAudit() method to open edit form
    - Implement handleDeleteAudit() method with confirmation dialog
    - Implement showConfirmationDialog() method
    - Implement refreshAuditList() method
    - Sort audits in reverse chronological order
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7, 5.8, 5.9, 5.10, 5.11_
  
  - [ ]* 9.3 Write integration tests for My Audits CRUD
    - Test audit creation with database
    - Test audit update with database
    - Test audit deletion with cascade behavior
    - Test audit retrieval and filtering
    - _Requirements: 5.9, 5.10_

- [ ] 10. Implement audit create/edit forms
  - [ ] 10.1 Create audit-form.fxml
    - Design form with fields: name, category, scores, findings, date
    - Add validation indicators for required fields
    - Add Save and Cancel buttons
    - _Requirements: 5.10, 16.4_
  
  - [ ] 10.2 Add form handling to MyAuditsController
    - Implement form validation logic
    - Implement save handler to call AuditService
    - Display success/error messages
    - Handle validation errors with field highlighting
    - _Requirements: 5.10, 16.1, 16.2, 16.4_

- [ ] 11. Checkpoint - Verify basic UI functionality
  - Test navigation between all sections
  - Test audit CRUD operations end-to-end
  - Verify data persistence in database
  - Test error handling and user feedback
  - Ensure all tests pass
  - Ask the user if questions arise

### Phase 3: Advanced Features

- [ ] 12. Implement analytics section with charts
  - [ ] 12.1 Create analytics.fxml
    - Add VBox container for three chart sections
    - Add LineChart for score evolution over time
    - Add PieChart for category distribution
    - Add BarChart for audit comparison
    - Add placeholder messages for insufficient data
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.7_
  
  - [ ] 12.2 Create AnalyticsController.java
    - Implement initialize() method
    - Implement loadChartData() method to fetch audit data
    - Implement renderScoreEvolutionChart() method
    - Implement renderCategoryDistributionChart() method
    - Implement renderAuditComparisonChart() method
    - Implement handleChartInteraction() method for hover/click
    - Handle insufficient data case with placeholder message
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7_
  
  - [ ]* 12.3 Write unit tests for chart data preparation
    - Test data transformation for line chart
    - Test data aggregation for pie chart
    - Test data comparison logic for bar chart
    - _Requirements: 4.1, 4.2, 4.3_

- [ ] 13. Implement reports section
  - [ ] 13.1 Create reports.fxml
    - Design TableView with columns: Report Name, Audit, Generation Date, Actions
    - Add "Generate New Report" button
    - Add progress indicator for report generation
    - _Requirements: 6.5, 6.6_
  
  - [ ] 13.2 Create ReportsController.java
    - Implement initialize() method
    - Implement loadReports() method to populate table
    - Implement handleGenerateReport() method with async PDF generation
    - Implement handleViewReport() method to open PDF
    - Implement handleDownloadReport() method
    - Implement showProgressIndicator() during generation
    - Display success message when report is generated
    - _Requirements: 6.1, 6.2, 6.4, 6.5, 6.6, 6.7, 16.1_
  
  - [ ]* 13.3 Write integration tests for report generation
    - Test PDF generation with sample audit data
    - Verify PDF file is created and valid
    - Test report metadata persistence
    - _Requirements: 6.2, 6.3_

- [ ] 14. Implement notifications section
  - [ ] 14.1 Create notifications.fxml
    - Design ListView for notification items
    - Add "Mark All as Read" button
    - Add filter dropdown for notification types
    - Style read vs unread notifications differently
    - _Requirements: 7.4, 7.5, 7.8_
  
  - [ ] 14.2 Create NotificationsController.java
    - Implement initialize() method
    - Implement loadNotifications() method
    - Implement handleNotificationClick() method to navigate to related content
    - Implement markAsRead() method
    - Implement markAllAsRead() method
    - Implement filterByType() method
    - Update notification badge in header when notifications are read
    - _Requirements: 7.4, 7.5, 7.6, 7.7, 7.8_
  
  - [ ]* 14.3 Write unit tests for notification handling
    - Test notification loading and filtering
    - Test mark as read functionality
    - Test badge count updates
    - _Requirements: 7.6, 7.7, 7.8_

- [ ] 15. Implement settings and profile management
  - [ ] 15.1 Create settings.fxml
    - Design form for profile editing (name, email, phone)
    - Add password change section with current/new/confirm fields
    - Add notification preferences checkboxes
    - Add dark mode toggle switch
    - _Requirements: 8.1, 8.4, 8.7, 11.1_
  
  - [ ] 15.2 Create SettingsController.java
    - Implement initialize() method to load current user data
    - Implement loadUserProfile() method
    - Implement handleSaveProfile() method with validation
    - Implement handleChangePassword() method with current password verification
    - Implement handleToggleDarkMode() method
    - Implement handleNotificationPreferences() method
    - Implement validateProfileData() method
    - Display success/error messages for all operations
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5, 8.6, 8.7, 8.8, 8.9, 11.1, 16.1, 16.2, 16.4_
  
  - [ ]* 15.3 Write unit tests for settings validation
    - Test profile data validation
    - Test password validation (current password check, new password requirements)
    - Test preference persistence
    - _Requirements: 8.2, 8.5, 8.6_

- [ ] 16. Extend UserService for dashboard operations
  - [ ] 16.1 Add dashboard-specific methods to UserService.java
    - Implement getUserPreferences() method
    - Implement saveUserPreferences() method
    - Implement updateUserProfile() method with validation
    - Ensure all queries filter by authenticated user ID
    - _Requirements: 8.1, 8.2, 8.3, 8.9, 14.3, 14.4_
  
  - [ ]* 16.2 Write unit tests for UserService extensions
    - Test preference retrieval and persistence
    - Test profile update validation
    - Test user ID filtering for data isolation
    - _Requirements: 14.3, 14.4_

- [ ] 17. Checkpoint - Verify advanced features
  - Test analytics charts with various data sizes
  - Test PDF report generation and viewing
  - Test notification system end-to-end
  - Test settings and profile updates
  - Verify all data is properly filtered by user ID
  - Ensure all tests pass
  - Ask the user if questions arise

### Phase 4: Bonus Features

- [ ] 18. Implement search functionality
  - [ ] 18.1 Add search bar to professional-dashboard.fxml
    - Add TextField for search input in prominent location
    - Add search icon button
    - _Requirements: 9.1_
  
  - [ ] 18.2 Implement search logic in controllers
    - Add search filtering to MyAuditsController
    - Add search filtering to ReportsController
    - Add search filtering to NotificationsController
    - Implement real-time filtering as user types
    - Highlight matching text in results
    - Restore full list when search is cleared
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6_
  
  - [ ]* 18.3 Write unit tests for search functionality
    - Test search filtering logic
    - Test real-time updates
    - Test search across multiple fields
    - _Requirements: 9.2, 9.3_

- [ ] 19. Implement recommendation engine
  - [ ] 19.1 Create RecommendationService.java
    - Implement generateRecommendations() method to analyze audit history
    - Implement pattern detection for low scores by category
    - Implement getActiveRecommendations() method
    - Implement dismissRecommendation() method
    - _Requirements: 10.1, 10.2, 10.5, 10.6, 10.7_
  
  - [ ] 19.2 Add recommendations display to dashboard-home.fxml
    - Add card or section for displaying recommendations
    - Show recommendation text, category, and priority
    - Add dismiss button for each recommendation
    - _Requirements: 10.3, 10.4, 10.6_
  
  - [ ] 19.3 Integrate recommendations into DashboardHomeController
    - Load recommendations on dashboard home view
    - Handle recommendation dismissal
    - Trigger recommendation generation after audit completion
    - Create notifications for new recommendations
    - _Requirements: 10.3, 10.4, 10.5, 10.6, 10.7_
  
  - [ ]* 19.4 Write unit tests for recommendation engine
    - Test pattern detection logic
    - Test recommendation generation for various score scenarios
    - Test dismissal functionality
    - _Requirements: 10.1, 10.2, 10.7_

- [ ] 20. Implement dark mode
  - [ ] 20.1 Create professional-dashboard-dark.css
    - Define dark color palette
    - Define dark styles for all UI components
    - Ensure sufficient contrast for readability
    - Adapt chart colors for dark mode
    - _Requirements: 11.2, 11.6, 11.7_
  
  - [ ] 20.2 Implement dark mode toggle logic
    - Load user's dark mode preference on login
    - Apply dark stylesheet when dark mode is enabled
    - Remove dark stylesheet when dark mode is disabled
    - Persist preference to database
    - _Requirements: 11.1, 11.2, 11.3, 11.4, 11.5_
  
  - [ ]* 20.3 Manual testing for dark mode
    - Verify all components are visible in dark mode
    - Verify charts adapt colors correctly
    - Verify contrast ratios meet WCAG AA standards
    - _Requirements: 11.6, 11.7_

- [ ] 21. Implement audit scheduling
  - [ ] 21.1 Create ScheduleService.java
    - Implement getScheduledAudits() method
    - Implement createSchedule() method
    - Implement updateSchedule() method
    - Implement deleteSchedule() method
    - Implement getUpcomingReminders() method
    - _Requirements: 12.4, 12.6, 12.7_
  
  - [ ] 21.2 Create schedule-audit-form.fxml
    - Design form with fields: audit name, scheduled date, scheduled time, category
    - Add Save and Cancel buttons
    - _Requirements: 12.2, 12.3_
  
  - [ ] 21.3 Add scheduling UI to MyAuditsController
    - Add "Schedule Audit" button
    - Implement handleScheduleAudit() method to open scheduling form
    - Display scheduled audits in separate section or with visual distinction
    - Implement edit and cancel handlers for scheduled audits
    - _Requirements: 12.1, 12.5, 12.7, 12.8_
  
  - [ ] 21.4 Implement reminder notification system
    - Create background task to check for upcoming scheduled audits
    - Generate reminder notifications when scheduled date arrives
    - Mark reminders as sent to avoid duplicates
    - _Requirements: 12.6_
  
  - [ ]* 21.5 Write integration tests for scheduling
    - Test schedule creation and persistence
    - Test schedule retrieval and filtering
    - Test reminder notification generation
    - _Requirements: 12.4, 12.6_

- [ ] 22. Implement role-based routing in Main.java
  - [ ] 22.1 Update Main.java or LoginController.java
    - After successful authentication, check user role
    - If role is "User", load professional-dashboard.fxml
    - If role is "Admin", load existing admin dashboard
    - Pass authenticated user and services to dashboard controller
    - _Requirements: 1.1, 14.1, 14.2, 14.5_
  
  - [ ]* 22.2 Write integration tests for role-based routing
    - Test that User role routes to professional dashboard
    - Test that Admin role routes to admin dashboard
    - Test that unauthenticated access redirects to login
    - _Requirements: 1.1, 1.2, 14.1, 14.5_

- [ ] 23. Implement session management and authentication checks
  - [ ] 23.1 Add session validation to ProfessionalDashboardController
    - Check session validity on dashboard load
    - Redirect to login if session is invalid or expired
    - Maintain authentication state throughout navigation
    - _Requirements: 1.2, 1.3, 1.4_
  
  - [ ]* 23.2 Write unit tests for session management
    - Test session validation logic
    - Test redirect behavior on invalid session
    - Test authentication state maintenance
    - _Requirements: 1.2, 1.3, 1.4_

- [ ] 24. Implement data access control and security
  - [ ] 24.1 Add user ID validation to all service methods
    - Verify that authenticated user ID matches requested data user ID
    - Throw SecurityException if user attempts to access another user's data
    - Add logging for security violations
    - _Requirements: 1.5, 14.3, 14.4_
  
  - [ ]* 24.2 Write security tests
    - Test that users cannot access other users' audits
    - Test that users cannot access other users' reports
    - Test that users cannot access other users' notifications
    - Test that direct data access attempts are blocked
    - _Requirements: 1.5, 14.3, 14.4_

- [ ] 25. Implement performance optimizations
  - [ ] 25.1 Add asynchronous data loading
    - Use JavaFX Task for long-running operations
    - Load dashboard data asynchronously to prevent UI blocking
    - Display loading indicators during async operations
    - _Requirements: 15.4, 15.5_
  
  - [ ] 25.2 Implement data caching
    - Cache frequently accessed user data (preferences, profile)
    - Cache audit list with invalidation on updates
    - Reduce redundant database queries
    - _Requirements: 15.6_
  
  - [ ]* 25.3 Performance testing
    - Test dashboard load time with various data sizes
    - Test chart rendering performance with 100+ audits
    - Test navigation transition times
    - Verify performance meets requirements (<2s load, <500ms navigation)
    - _Requirements: 15.1, 15.2, 15.3_

- [ ] 26. Implement comprehensive error handling
  - [ ] 26.1 Add error handling to all controllers
    - Wrap service calls in try-catch blocks
    - Display user-friendly error dialogs
    - Log errors with context (user ID, operation, timestamp)
    - Provide recovery options (retry, cancel)
    - _Requirements: 16.1, 16.2, 16.3, 16.4, 16.7_
  
  - [ ] 26.2 Implement success feedback
    - Display success messages for all user actions
    - Auto-dismiss success messages after 3 seconds
    - Use consistent styling for feedback messages
    - _Requirements: 16.1, 16.5_
  
  - [ ]* 26.3 Write error handling tests
    - Test error dialog display for various error types
    - Test success message display and auto-dismiss
    - Test error logging
    - _Requirements: 16.1, 16.2, 16.5_

- [ ] 27. Implement accessibility features
  - [ ] 27.1 Add keyboard navigation support
    - Ensure all interactive elements are keyboard accessible
    - Add visible focus indicators
    - Test tab order for logical flow
    - _Requirements: 18.2, 18.3_
  
  - [ ] 27.2 Add ARIA labels and text alternatives
    - Add labels for all icons and buttons
    - Add text alternatives for charts
    - Add tooltips for complex features
    - _Requirements: 18.4, 18.5, 18.7_
  
  - [ ] 27.3 Verify color contrast
    - Check all text/background combinations meet WCAG AA standards
    - Adjust colors if necessary
    - Test in both light and dark modes
    - _Requirements: 18.1_
  
  - [ ]* 27.4 Manual accessibility testing
    - Test keyboard navigation through all sections
    - Test with screen reader (if available)
    - Verify focus indicators are visible
    - Verify tooltips display correctly
    - _Requirements: 18.2, 18.3, 18.4, 18.5, 18.7_

- [ ] 28. Final integration and polish
  - [ ] 28.1 Create professional-dashboard.css for consistent styling
    - Define color palette for light mode
    - Define card styles with shadows and borders
    - Define button hover effects
    - Define chart color schemes
    - Define notification badge styles
    - Ensure consistent spacing and typography
    - _Requirements: 13.5, 13.6, 13.7_
  
  - [ ] 28.2 Apply CSS to all FXML files
    - Link stylesheet to all dashboard FXML files
    - Test visual consistency across all sections
    - _Requirements: 13.5, 13.6, 13.7_
  
  - [ ] 28.3 Create database migration script with test data
    - Create SQL script to insert sample audits, notifications, reports
    - Document how to run the migration script
    - _Requirements: 17.1_

- [ ] 29. Final checkpoint - End-to-end testing
  - Test complete user workflow from login to logout
  - Test all CRUD operations across all sections
  - Test role-based access control
  - Test error handling and recovery
  - Test performance with realistic data volumes
  - Verify all accessibility features work correctly
  - Verify dark mode works across all sections
  - Run all automated tests and ensure they pass
  - Ask the user if questions arise

## Notes

- Tasks marked with `*` are optional testing tasks and can be skipped for faster MVP delivery
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation and provide opportunities for user feedback
- The implementation follows a layered approach: data layer → service layer → presentation layer
- All database operations use PreparedStatement to prevent SQL injection
- All service methods validate user ID to ensure data isolation
- Asynchronous operations use JavaFX Task to prevent UI blocking
- Error handling follows a consistent pattern: catch, log, display user-friendly message
