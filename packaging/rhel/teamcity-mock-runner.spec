%global plugindir   /var/teamcity/.BuildServer/plugins

Summary:        TeamCity plugin for building RPMs using Mock
Name:           teamcity-mock-runner
Version:        0.1
Release:        1%{?dist}
Group:          Development/Tools/Other
Source:         %{name}-%{version}.tar.gz
License:        GPL3
Packager:       Justin Salmon <jsalmon@cern.ch>
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)
BuildArch:      noarch
BuildRequires:  ant >= 1.7.1

%description
TeamCity plugin for building RPMs using Mock

%prep
%setup

%build
ant dist

%install
mkdir -p %{buildroot}%plugindir
install -pm 755 dist/mock-runner.zip %{buildroot}/%plugindir

%files
%plugindir/mock-runner.zip
