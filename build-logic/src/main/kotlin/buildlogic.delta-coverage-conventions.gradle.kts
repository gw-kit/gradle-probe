import io.github.surpsg.deltacoverage.CoverageEngine

plugins {
    base
    id("io.github.surpsg.delta-coverage")
}

deltaCoverageReport {
    coverage.engine = CoverageEngine.INTELLIJ

    diffSource.byGit {
        diffBase = project.properties["diffBase"]?.toString() ?: "refs/remotes/origin/main"
        useNativeGit = true
    }

    reports {
        html = true
        xml = true
        console = true
    }

    violationRules.failIfCoverageLessThan(0.9)
}

tasks.named("gitDiff") {
    outputs.upToDateWhen { false }
}