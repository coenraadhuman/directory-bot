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