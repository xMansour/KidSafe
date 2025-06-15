# Authentication Flow

This document outlines the authentication process within the application, including user registration, login, account management, and data storage.

## 1. User Registration (Sign-Up)

### Email/Password

- **UI Elements:**
    - `EditText etName`: For user's name.
    - `EditText etEmail`: For user's email address.
    - `EditText etPassword`: For user's password.
    - `EditText etPasswordConfirm`: For confirming the password.
    - `RadioGroup rgUserType`: To select user type (Parent/Child).
    - `RadioButton rbParent`: For selecting Parent user type.
    - `RadioButton rbChild`: For selecting Child user type.
    - `EditText txtParentEmail`: (Visible if Child type is selected) For child to enter parent's email.
    - `ImageView imgUserProfile`: To display the selected profile image.
    - `Button btnSignUp`: To initiate the sign-up process.
    - `TextView tvLogin`: To navigate to the Login screen.
    - `ProgressBar progressBar`: To indicate loading.

- **Validation Checks:**
    - Name: Cannot be empty.
    - Email: Must be a valid email format and cannot be empty.
    - Password:
        - Cannot be empty.
        - Must be at least 6 characters long.
        - Must match the confirmed password.
    - User Type: Must be selected.
    - Parent Email (for Child accounts):
        - Cannot be empty.
        - Must be a valid email format.
        - Must be different from the child's email.
    - Profile Image: A profile image must be selected.

- **User Creation:**
    - `FirebaseAuth.createUserWithEmailAndPassword(email, password)` is called to create a new user account in Firebase Authentication.
    - Upon successful creation, a verification email is sent to the user: `firebaseUser.sendEmailVerification()`.

- **Data Storage (Firebase Realtime Database):**
    - After successful Firebase Authentication user creation, user details are stored in the Firebase Realtime Database.
    - If the user type is "Parent":
        - Data is stored under `/users/parents/{uid}`.
        - Information stored: `name`, `email`, `profileImage` (initially empty, updated after image upload).
    - If the user type is "Child":
        - Data is stored under `/users/childs/{uid}`.
        - Information stored: `name`, `email`, `parentEmail`, `profileImage` (initially empty, updated after image upload).
    - The `uid` is the User ID obtained from the successfully created `FirebaseUser`.

- **User Type Selection & `txtParentEmail`:**
    - Users select their type using `rgUserType`.
    - If "Child" is selected, `txtParentEmail` becomes visible, and the child must provide their parent's email address. This email is stored alongside other child details.

- **Profile Image Uploading:**
    - The user selects a profile image from their device using an `Intent` to pick an image.
    - The selected image (`filePathUri`) is uploaded to Firebase Storage under the `profileImages/{uid}` path.
    - Upon successful upload, the download URL of the image is retrieved.
    - This download URL is then saved to the user's record in the Firebase Realtime Database (in the `profileImage` field of their parent or child node).

### Google Sign-In

- **UI Elements:**
    - `SignInButton btnGoogleSignIn`: The standard Google Sign-In button.
    - `ProgressBar progressBar`: To indicate loading.

- **Initiating Google Sign-In:**
    - A `GoogleSignInOptions` object is configured with `requestIdToken` using the default web client ID.
    - `GoogleSignInClient` is created using these options.
    - An `Intent` is launched via `googleSignInClient.getSignInIntent()` to start the Google Sign-In flow.
    - The result is handled in `onActivityResult`.

- **Firebase Authentication with Google:**
    - After successfully signing in with Google, an `AuthCredential` is obtained using `GoogleAuthProvider.getCredential(account.getIdToken(), null)`.
    - `FirebaseAuth.signInWithCredential(credential)` is called to authenticate the user with Firebase.

- **Data Storage (Firebase Realtime Database):**
    - If Firebase authentication is successful:
        - User data (name, email, photo URL) is retrieved from the `GoogleSignInAccount` object.
        - The system checks if the user already exists in `/users/parents/{uid}` or `/users/childs/{uid}`.
        - If the user is new:
            - `GoogleChildSignUpDialogFragment` is shown to determine if the account is for a Parent or Child.
            - If "Child" is selected, the dialog also prompts for the parent's email.
            - Based on the selection, the user's data (name from Google, email from Google, profileImage URL from Google, and parentEmail if applicable) is stored under `/users/parents/{uid}` or `/users/childs/{uid}`.
        - If the user already exists, they are redirected to the appropriate activity (`ParentSignedInActivity` or `ChildSignedInActivity`).

- **`GoogleChildSignUpDialogFragment`:**
    - This dialog is displayed when a new user signs up using Google Sign-In.
    - It allows the user to specify whether they are a "Parent" or a "Child".
    - If "Child" is selected, it includes an `EditText` for the child to enter their parent's email address.
    - The selected user type and parent's email (if applicable) are used to store the user's data correctly in the Realtime Database.

## 2. User Login

### Email/Password

- **UI Elements:**
    - `EditText etEmail`: For user's email address.
    - `EditText etPassword`: For user's password.
    - `CheckBox cbRememberMe`: For the "Remember Me" functionality.
    - `Button btnLogin`: To initiate the login process.
    - `TextView tvSignUp`: To navigate to the Sign-Up screen.
    - `TextView tvForgotPassword`: To initiate password recovery.
    - `ProgressBar progressBar`: To indicate loading.

- **Validation Checks:**
    - Email: Must be a valid email format and cannot be empty.
    - Password: Cannot be empty.

- **Signing In:**
    - `FirebaseAuth.signInWithEmailAndPassword(email, password)` is called.

- **"Remember Me" Functionality:**
    - If `cbRememberMe` is checked:
        - `SharedPrefsUtils.setAutoLoginPrefs(context, true)` is called.
        - Email and password are saved in shared preferences using `SharedPrefsUtils.saveLoginCredentials(context, email, password)`.
    - If not checked:
        - `SharedPrefsUtils.setAutoLoginPrefs(context, false)` is called.
        - Saved credentials are cleared using `SharedPrefsUtils.clearLoginCredentials(context)`.

- **Account Verification & Redirection:**
    - After successful sign-in with Firebase, it checks if the user's email is verified using `firebaseUser.isEmailVerified()`. (Note: The prompt mentions `Validators.isVerified(user)`, but the code in `LoginActivity` directly uses `firebaseUser.isEmailVerified()`).
    - If the email is not verified, the user is redirected to `AccountVerificationActivity`.
    - If verified, the application then determines the user type:
        - It first checks the `/users/parents/{uid}` path in the Realtime Database.
        - If the user's UID exists there, they are considered a "Parent" and redirected to `ParentSignedInActivity`.
        - If not found under parents, it checks the `/users/childs/{uid}` path.
        - If the user's UID exists there, they are considered a "Child" and redirected to `ChildSignedInActivity`.
        - If the UID is not found in either path (which implies an issue, as users should be registered with a type), an error message is typically shown.

### Google Sign-In

- **UI Elements:**
    - `SignInButton btnGoogleSignIn`: The standard Google Sign-In button.
    - `ProgressBar progressBar`: To indicate loading.

- **Initiating Google Sign-In:**
    - Similar to registration, `GoogleSignInClient` is used to get a sign-in intent.
    - The result is handled in `onActivityResult`.

- **Firebase Authentication:**
    - An `AuthCredential` is obtained from the Google account's ID token.
    - `FirebaseAuth.signInWithCredential(credential)` is used to sign in to Firebase.

- **User Type Determination and Navigation:**
    - After successful Firebase authentication with Google:
        - The system checks if the user's UID exists under `/users/parents/{uid}`. If yes, navigate to `ParentSignedInActivity`.
        - If not, it checks under `/users/childs/{uid}`. If yes, navigate to `ChildSignedInActivity`.
        - If the user is new (i.e., UID not found in the database, which can happen if they authenticated with Google but didn't complete the initial type selection), they are prompted with `GoogleChildSignUpDialogFragment` to select their role (Parent/Child) and provide a parent's email if they are a child. After this, their data is saved to the database, and they are then navigated to the appropriate activity.

### Auto-Login

- **`SharedPrefsUtils` Usage:**
    - If the "Remember Me" checkbox was checked during a previous login, `SharedPrefsUtils` stores:
        - A boolean flag `autoLoginPrefs` set to `true`.
        - The user's email and password.
- **Automatic Login Process:**
    - On app start (`LoginActivity`'s `onStart` or `onCreate`), the app checks `SharedPrefsUtils.getAutoLoginPrefs(context)`.
    - If `true`:
        - It retrieves the saved email and password using `SharedPrefsUtils.getLoginCredentials(context)`.
        - It attempts to log in the user automatically using `FirebaseAuth.signInWithEmailAndPassword(email, password)`.
        - If successful, it proceeds with the usual verification check and redirection to the parent or child dashboard.
        - If auto-login fails (e.g., password changed, account deleted), the user remains on the `LoginActivity` to log in manually.

## 3. Account Management

(Based on `AccountUtils.java` and relevant activity code)

- **Password Reset (`sendPasswordRecoveryEmail` in `LoginActivity.java`):**
    - Triggered by `tvForgotPassword` in `LoginActivity`.
    - Prompts the user for their email address.
    - Calls `FirebaseAuth.getInstance().sendPasswordResetEmail(email)`.
    - Shows a confirmation or error message to the user.

- **Password Change (`changePassword` in `AccountUtils.java`):**
    - Typically accessed from a user profile or settings screen (not detailed in `LoginActivity` or `SignUpActivity`).
    - Takes the new password as input.
    - Gets the current `FirebaseUser`.
    - Calls `currentUser.updatePassword(newPassword)`.
    - Provides feedback on success or failure.

- **Account Deletion (`deleteAccount` in `AccountUtils.java`):**
    - Gets the current `FirebaseUser` and their UID.
    - Determines if the user is a "Parent" or "Child" by checking their presence in `/users/parents/{uid}` or `/users/childs/{uid}` respectively.
    - **Deletes data from Realtime Database:**
        - Removes the user's node from either `/users/parents/{uid}` or `/users/childs/{uid}`.
    - **Deletes profile image from Firebase Storage:**
        - Creates a reference to `profileImages/{uid}`.
        - Calls `storageReference.delete()`.
    - **Deletes Firebase Authentication record:**
        - Calls `currentUser.delete()`.
    - Handles potential errors and provides feedback (e.g., requires recent login for deletion).

## 4. User Data Storage

- **Firebase Authentication:**
    - Stores core user authentication details:
        - User ID (UID)
        - Email address
        - Linked authentication providers (e.g., email/password, Google)
        - Password hashes (for email/password authentication)

- **Firebase Realtime Database (`/users`):**
    - **`/parents/{uid}`:**
        - Stores details for users registered as "Parent".
        - Each child node is a UID.
        - Attributes:
            - `name`: String (User's full name)
            - `email`: String (User's email address)
            - `profileImage`: String (URL of the profile image stored in Firebase Storage)
            - `userType`: String (Value: "parent")
    - **`/childs/{uid}`:**
        - Stores details for users registered as "Child".
        - Each child node is a UID.
        - Attributes:
            - `name`: String (User's full name)
            - `email`: String (User's email address)
            - `parentEmail`: String (Email address of the child's parent)
            - `profileImage`: String (URL of the profile image stored in Firebase Storage)
            - `userType`: String (Value: "child")

- **Firebase Storage (`profileImages`):**
    - Stores user profile pictures.
    - Files are typically named by the user's UID (e.g., `profileImages/{uid}.jpg` or `profileImages/{uid}`).
    - The download URL of the uploaded image is what's stored in the Realtime Database.
