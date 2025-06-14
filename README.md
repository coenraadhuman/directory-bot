# directory-bot

Dockerized application meant to provide the following functionality between a source directory and a target directory:

- Symlink all files (with an absolute path) from a source directory to a target directory.
- Move any non-symlinked files that exist on the target directory to the primary directory.
- Clean invalid symlinks in target directory.

## Docker

Example of running the application:

MacOS:
```bash
docker run --user "$(id -u):$(id -g)" -e APP_UID="$(id -u)" -e APP_GID="$(id -g)" -v /your/machine/root/directory:/working-directory -v /your/machine/config/directory/for/application:/config directory-bot:0.6.4
```