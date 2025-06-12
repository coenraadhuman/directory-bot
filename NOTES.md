LETS START HERE:
- Symlink any files (with an absolute path source) that exist on the source directory to the target directory.

FUTURE:
- Move any non-symlinked files that exist on the target directory to the primary directory.
- Remove any files that were deleted on the source directory from the target directory.
- Remove any files that were deleted on the target directory from the source directory.
- Rename symlink name via external tool for target directory from source directory.
- Update symlink root directory and target if the source directory file updates. Vice-versa?

thinking track diffs
-> start with empty target, makes most sense and database tracks of everything



PROCESS:

1. Load properties
2. Migrate database
3. Get database connection
4. Get source directory from properties
5. Get target directory from properties
6. Iterate over all source directory files
   - Determine target directory
   - Determine target path
   - Does subdirectories exist in target? If not create.
   - Does the target exist as a symbolic link? 
     - No, then create symbolic link.
     - Yes:
       - Is the symbolic link still valid, points to existing target? If not delete and create new one.
       - Is the old symbolic link the target the same as what was asked now.
         - No, delete and create new.
         - Yes, skip creation.