FUTURE:
- Remove any files that were deleted on the source directory from the target directory.
- Remove any files that were deleted on the target directory from the source directory.
- Rename symlink name via external tool for target directory from source directory.
- Update symlink root directory and target if the source directory file updates. Vice-versa?

thinking track diffs
-> start with empty target, makes most sense and database tracks of everything

MacOS:
```bash
docker run --user "$(id -u):$(id -g)" -e APP_UID="$(id -u)" -e APP_GID="$(id -g)" -v /Users/coenraadhuman/Development/personal/directory-bot-test:/working-directory -v /Users/coenraadhuman/Development/personal/directory-bot-config:/config directory-bot:0.0.1
```