var gulp = require('gulp'),
  ts = require('gulp-typescript'),
  // concat = require('gulp-concat'),
  tsconfig = require('./tsconfig.json'),
  htmlPattern = './src/templates/**',
  cssPattern = './src/styles/**',
  tsProject = ts.createProject('./tsconfig.json', {
    sortOutput: true
  });

gulp.task('html', function() {
  gulp.src(htmlPattern)
    .pipe(gulp.dest('./target/templates'));
});

gulp.task('css', function() {
  gulp.src(cssPattern)
    .pipe(gulp.dest('./target/styles'));
});

gulp.task('ts', function() {
  var tsSources = tsProject.src()
    .pipe(ts(tsProject)).js;

  return tsSources
    // .pipe(concat('jmxhealth-tray.js'))
    .pipe(gulp.dest(tsconfig.compilerOptions.outDir));
});

gulp.task('lib', function() {
  gulp.src([
    './node_modules/angular/angular.js',
    './node_modules/angular-animate/angular-animate.js',
    './node_modules/angular-aria/angular-aria.js',
    './node_modules/angular-messages/angular-messages.js',
    './node_modules/angular-material/angular-material.js',
    './node_modules/angular-material/angular-material.css'
  ])
    .pipe(gulp.dest('./target/lib'));
});

gulp.task('build', ['ts', 'html', 'lib', 'css']);

gulp.task('watch', ['build'], function() {
  gulp.watch(tsconfig.filesGlob, ['ts']);
  gulp.watch(htmlPattern, ['html']);
  gulp.watch(cssPattern, ['css']);
});
